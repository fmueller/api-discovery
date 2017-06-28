#!/usr/bin/env python
"""
CDG Monorepo Handler.
"""

import re
from os import environ
from subprocess import check_call
import requests

GH_URL = 'https://api.github.com'
SUBPROJECT_COMMANDS = {
    'crawler': './.delivery/build-crawler.sh',
    'storage': './.delivery/build-storage.sh',
    'swagger-ui-3': './.delivery/build-swagger-ui-3.sh',
    '.delivery': './.delivery/root.sh',
    'delivery.yaml': './.delivery/root.sh',
}


def get_arguments():
    """
    Get script arguments from environement variables.
    See https://pages.github.bus.zalan.do/continuous-delivery/cdp-docs/user-guide/index.html#build-variables
    """
    sha = environ['CDP_TARGET_COMMIT_ID']
    repo_path = environ['CDP_TARGET_REPOSITORY']
    (user, repo) = repo_path.split('/')[-2:]
    if user is None or repo is None:
        raise Exception('Could not parse %s' % repo_path)
    return (user, repo, sha)


def get_pr_number(user, repo, sha):
    """
    Get the pull request number.
    See https://developer.github.com/v3/git/commits/#get-a-commit
    """
    # Use the CDP pull request number if possible.
    if 'CDP_PULL_REQUEST_NUMBER' in environ:
        return int(environ['CDP_PULL_REQUEST_NUMBER'])

    # Look for a pull request number in the commit message.
    r = requests.get('%s/repos/%s/%s/git/commits/%s' %
                     (GH_URL, user, repo, sha))
    commit_message = r.json()['message']
    match = re.match(r'.* #(\d+) .*', commit_message)
    if match is None:
        return None
    return int(match.group(1))


def get_pr_changed_files(user, repo, number):
    """
    Get the changed files in a pull request.
    See https://developer.github.com/v3/pulls/#list-pull-requests-files
    """
    r = requests.get('%s/repos/%s/%s/pulls/%s/files' %
                     (GH_URL, user, repo, number))
    return [file['filename'] for file in r.json()]


def get_root_dirname(filename):
    """Get the root directory of a filename."""
    segments = filename.split('/')
    if len(segments) > 1:
        return segments[0]
    else:
        return filename


def get_directories(filenames):
    """Get the set of directories for a list of filenames."""
    return set(get_root_dirname(f) for f in filenames)


def execute_commands(directories):
    """Execute commands in the given directories."""
    for d in directories:
        if d in SUBPROJECT_COMMANDS:
            cmd = SUBPROJECT_COMMANDS[d]
            check_call(cmd.split())
        else:
            print('Skip %s' % d)


def main():
    """Main method."""
    print('Run CDG monorepo handler.')
    print(environ)
    (user, repo, sha) = get_arguments()
    number = get_pr_number(user, repo, sha)
    if number is None:
        raise Exception('No pull request number for commit %s.' % sha)
    files = get_pr_changed_files(user, repo, number)
    dirs = get_directories(files)
    print('Modified directories in pull request #%d: %s' %
          (number, ', '.join(dirs)))
    execute_commands(dirs)


if __name__ == "__main__":
    main()
