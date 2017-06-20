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
      {url && <div><a href={url} target="_blank">{name} - Website</a></div>}
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
      {url ? <a target="_blank" href={url}>{name}</a> : <span>{name}</span>}
    </div>
  );
};

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
    const externalDocs = specSelectors.externalDocs();

    const apiVersions = apiDiscoverySelectors.apiVersions() as ApiVersion[];
    const selectedApiVersion = apiDiscoverySelectors.selectedApiVersion() as SelectedApiVersion;
    if (!selectedApiVersion) return null;
    const application = selectedApiVersion.applications[0];

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
            {apiVersions.map((apiVersion, i) =>
              <small
                key={i}
                className={apiVersion.api_version === version ? 'selected' : undefined}
                onClick={this.onSelectApiVersion.bind(this, apiVersion)}
              >
                <pre className="version">{apiVersion.api_version}</pre>
              </small>
            )}
          </h2>
          <p>
            <a target="_blank" href={application.href}>
              <span className="url">{application.href}</span>
            </a>
          </p>
          {url &&
            <p>
              <a target="_blank" href={url}>
                <span className="url">{url}</span>
              </a>
            </p>}
        </hgroup>

        <div className="description">
          <div>
            <table style={{ textAlign: 'left', maxWidth: '400px' }}>
              <thead>
                <tr>
                  <th style={{ padding: '8px 0px', minWidth: '200px' }}>created at</th>
                  <th style={{ padding: '8px 0px' }}>updated at</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td style={{ padding: '8px 0px' }}>
                    {new Date(application.created).toLocaleString()}
                  </td>
                  <td style={{ padding: '8px 0px' }}>
                    {new Date(application.last_updated).toLocaleString()}
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <Markdown source={description} />
        </div>

        {termsOfService &&
          <div>
            <a target="_blank" href={termsOfService}>Terms of service</a>
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
