import React = require('react');
import PropTypes = require('prop-types');
import Select = require('react-select');
import Logo = require('../img/swagger.png');
import log from '../framework/debug';

import ApiMetaData from '../../common/domain/model/ApiMetaData';

type Props = {
  specSelectors: any;
  specActions: any;
  getComponent: (name: string, container?: boolean | 'root') => React.ComponentType<any>;
  apiDiscoveryActions: { [name: string]: (...args: any[]) => void };
  apiDiscoverySelectors: any;
};

/**
 * Based on the TopBar plugin.
 * See https://github.com/swagger-api/swagger-ui/tree/master/src/plugins/topbar
 */
export default () => ({
  components: {
    Topbar
  }
});

class Topbar extends React.Component<Props, undefined> {
  public static readonly propTypes = {
    specSelectors: PropTypes.object.isRequired,
    specActions: PropTypes.object.isRequired,
    getComponent: PropTypes.func.isRequired,
    apiDiscoveryActions: PropTypes.object.isRequired,
    apiDiscoverySelectors: PropTypes.object.isRequired
  };

  private onApiSelected(api: { label: string; value: string }) {
    this.props.apiDiscoveryActions.selectApi(api.value);
  }

  public async componentDidMount() {
    await this.props.apiDiscoveryActions.fetchApis();
    // Parse possible API id from the url scheme://host/apis/{id}
    const match = /^https?:\/\/[^\/]+\/apis\/([^\/\s]+)$/.exec(window.location.href);
    if (match && match[1]) {
      log('Select API from URL: %s', match[1]);
      this.props.apiDiscoveryActions.selectApi(match[1]);
    }
  }

  public render() {
    const { getComponent, specSelectors, apiDiscoverySelectors } = this.props;
    const Link = getComponent('Link');

    const isLoading = specSelectors.loadingStatus() === 'loading';
    const isFailed = specSelectors.loadingStatus() === 'failed';

    const selectorStyle: any = {};
    if (isFailed) selectorStyle.color = 'red';
    if (isLoading) selectorStyle.color = '#aaa';

    const selectedApi = apiDiscoverySelectors.selectedApi() as string;
    const apis = apiDiscoverySelectors.apis() as ApiMetaData[];
    const selectableApis = apis
      .slice()
      .filter(api => !!api.id)
      .sort()
      .map(api => ({ label: api.id, value: api.id, clearableValue: false }));

    return (
      <div className="topbar">
        <div className="wrapper">
          <div className="topbar-wrapper">
            <Link href="/" title="API Discovery">
              <img height="30" width="30" src={Logo} alt="Swagger UX" />
              <span className="topbarTitle">API Discovery</span>
            </Link>
            <div className="download-url-wrapper">
              <Select
                disabled={isLoading}
                style={selectorStyle}
                name="select-api"
                placeholder={isLoading ? 'loading …' : 'select …'}
                value={isLoading ? '' : selectedApi}
                options={selectableApis}
                clearable={false}
                onChange={this.onApiSelected.bind(this)}
              />
            </div>
          </div>
        </div>
      </div>
    );
  }
}
