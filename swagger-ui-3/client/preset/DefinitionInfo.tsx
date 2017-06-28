import React = require('react');
import PropTypes = require('prop-types');
import { Map } from 'immutable';

import ApiVersion from '../../common/domain/model/ApiVersion';
import { SelectedApiVersion } from './selectors';

const Contact = ({ data }: { data: Map<string, string> }) => {
  const name = data.get('name') || 'the developer';
  const url = data.get('url');
  const email = data.get('email');

  return (
    <div>
      {url &&
        <div>
          <a href={url} target="_blank">
            {name} - Website
          </a>
        </div>}
      {email &&
        <a href={`mailto:${email}`}>
          {url ? `Send email to ${name}` : `Contact ${name}`}
        </a>}
    </div>
  );
};

const License = ({ license }: { license: Map<string, string> }) => {
  const name = license.get('name') || 'License';
  const url = license.get('url');

  return (
    <div>
      {url
        ? <a target="_blank" href={url}>
            {name}
          </a>
        : <span>
            {name}
          </span>}
    </div>
  );
};

const Version = (props: { version: string; selected: boolean; onClick: () => any }) =>
  <small className={props.selected ? 'selected' : undefined} onClick={props.onClick}>
    <pre className="version">
      {props.version}
    </pre>
  </small>;

type Props = {
  specSelectors: any;
  getComponent: (name: string, container?: boolean | 'root') => React.ComponentType<any>;
  apiDiscoveryActions: any;
  apiDiscoverySelectors: any;
};

/**
 * Based on https://github.com/swagger-api/swagger-ui/blob/master/src/core/components/info.jsx
 */
export default class Info extends React.Component<Props, undefined> {
  public static readonly propTypes = {
    specSelectors: PropTypes.object.isRequired,
    getComponent: PropTypes.func.isRequired,
    apiDiscoveryActions: PropTypes.object.isRequired,
    apiDiscoverySelectors: PropTypes.object.isRequired
  };

  public shouldComponentUpdate(nextProps: Props) {
    return !!nextProps.apiDiscoverySelectors.selectedApiVersion();
  }

  private onSelectApiVersion(apiVersion: ApiVersion) {
    this.props.apiDiscoveryActions.selectApiVersion(apiVersion);
  }

  public render() {
    const { specSelectors, apiDiscoverySelectors, getComponent } = this.props;

    const info = specSelectors.info();
    const url = specSelectors.url();
    const basePath = specSelectors.basePath();
    const host = specSelectors.host();
    const externalDocs = specSelectors.externalDocs();

    const apiVersions = apiDiscoverySelectors.apiVersions() as ApiVersion[];
    const selectedApiVersion = apiDiscoverySelectors.selectedApiVersion() as SelectedApiVersion;
    if (!selectedApiVersion) return null;

    const version = info.get('version');
    const description = info.get('description');
    const title = info.get('title');
    const termsOfService = info.get('termsOfService');
    const contact = info.get('contact');
    const license = info.get('license');

    let externalDocsUrl = '';
    let externalDocsDescription = '';
    if (externalDocs) {
      externalDocsUrl = externalDocs.get('url');
      externalDocsDescription = externalDocs.get('description');
    }

    const Markdown = getComponent('Markdown');

    return (
      <div className="info">
        <hgroup className="main">
          <h2 className="title">
            {title}
            <span className="line-break-768" />
            {apiVersions
              .slice()
              .sort((a, b) => (a.api_version < b.api_version ? 1 : -1))
              .map((api, i) =>
                <Version
                  key={i}
                  version={api.api_version}
                  selected={api.api_version === version}
                  onClick={this.onSelectApiVersion.bind(this, api)}
                />
              )}
          </h2>
          {host && basePath
            ? <pre className="base-url">
                [ Base url: {host}
                {basePath}]
              </pre>
            : null}
          {url &&
            <p>
              <a target="_blank" href={url}>
                <span className="url">
                  {url}
                </span>
              </a>
            </p>}
        </hgroup>

        <div className="description">
          <Markdown source={description} />
        </div>

        {termsOfService &&
          <div>
            <a target="_blank" href={termsOfService}>
              Terms of service
            </a>
          </div>}

        {contact && contact.size ? <Contact data={contact} /> : null}
        {license && license.size ? <License license={license} /> : null}
        {externalDocsUrl
          ? <a target="_blank" href={externalDocsUrl}>
              {externalDocsDescription || externalDocsUrl}
            </a>
          : null}
      </div>
    );
  }
}
