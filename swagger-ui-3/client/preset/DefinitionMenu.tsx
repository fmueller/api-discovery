import React = require('react');
import PropTypes = require('prop-types');
import ApiVersion from '../../common/domain/model/ApiVersion';

type Props = {
  errSelectors: object;
  errActions: object;
  specActions: object;
  specSelectors: any;
  layoutSelectors: object;
  layoutActions: object;
  getComponent: (name: string, container?: boolean | 'root') => React.ComponentClass<any>;
  apiDiscoveryActions: any;
  apiDiscoverySelectors: any;
};

type State = {
  apiVersions: ApiVersion[];
  selectedApiVersion: ApiVersion;
};

export default class DefinitionMenu extends React.Component<Props, State> {
  public static readonly propTypes = {
    errSelectors: PropTypes.object.isRequired,
    errActions: PropTypes.object.isRequired,
    specActions: PropTypes.object.isRequired,
    specSelectors: PropTypes.object.isRequired,
    layoutSelectors: PropTypes.object.isRequired,
    layoutActions: PropTypes.object.isRequired,
    getComponent: PropTypes.func.isRequired,
    apiDiscoveryActions: PropTypes.object.isRequired,
    apiDiscoverySelectors: PropTypes.object.isRequired
  };

  constructor(props: Props, context: any) {
    super(props, context);
    this.state = {
      apiVersions: props.apiDiscoverySelectors.apiVersions(),
      selectedApiVersion: props.apiDiscoverySelectors.selectedApiVersion()
    };
  }

  public componentWillReceiveProps(nextProps: Props) {
    this.setState({
      apiVersions: nextProps.apiDiscoverySelectors.apiVersions(),
      selectedApiVersion: nextProps.apiDiscoverySelectors.selectedApiVersion()
    });
  }

  private onSelectVersion(apiVersion: ApiVersion) {
    this.props.apiDiscoveryActions.selectApiVersion(apiVersion);
  }

  public render() {
    if (!this.state.selectedApiVersion) return null;
    return (
      <div>
        <h4>versions</h4>
        <ul style={{ listStyleType: 'none' }}>
          {this.state.apiVersions.map(version =>
            <li key={version.api_version}>
              <button className="btn" onClick={this.onSelectVersion.bind(this, version)}>
                {version.api_version}
              </button>
            </li>
          )}
        </ul>
        <p>{this.state.selectedApiVersion.definitions[0].applications[0].api_url}</p>
        <p>{this.state.selectedApiVersion.definitions[0].applications[0].api_ui}</p>
        <p>{this.state.selectedApiVersion.definitions[0].applications[0].href}</p>
      </div>
    );
  }
}
