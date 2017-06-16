import React = require('react');
import PropTypes = require('prop-types');
import Select = require('react-select');
import log from '../framework/debug';
import Logo = require('../img/swagger.png');
import ApiMetaData from '../../common/domain/model/ApiMetaData';

type Props = {
  specSelectors: any;
  specActions: any;
  getComponent: (name: string, container?: boolean | 'root') => React.ComponentClass<any>;
  apiDiscoveryActions: { [name: string]: (...args: any[]) => void };
  apiDiscoverySelectors: any;
};

type State = {
  apis: ApiMetaData[];
  selectedApiId?: string;
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

class Topbar extends React.Component<Props, State> {
  public static readonly propTypes = {
    specSelectors: PropTypes.object.isRequired,
    specActions: PropTypes.object.isRequired,
    getComponent: PropTypes.func.isRequired,
    apiDiscoveryActions: PropTypes.object.isRequired,
    apiDiscoverySelectors: PropTypes.object.isRequired
  };

  constructor(props: Props, context: any) {
    super(props, context);
    this.state = {
      apis: props.apiDiscoverySelectors.apis()
    };
  }

  public componentWillReceiveProps(nextProps: Props) {
    this.setState({
      apis: nextProps.apiDiscoverySelectors.apis()
    });
  }

  private onApiSelected(api: { label: string; value: string }) {
    this.setState({ selectedApiId: api.value });
    this.props.apiDiscoveryActions.selectApi(api.value);
  }

  public componentDidMount() {
    log('Topbar did mount.', this.props, this.state);
    this.props.apiDiscoveryActions.fetchApis();
  }

  public render() {
    const { getComponent, specSelectors } = this.props;
    const Button = getComponent('Button');
    const Link = getComponent('Link');

    const isLoading = specSelectors.loadingStatus() === 'loading';
    const isFailed = specSelectors.loadingStatus() === 'failed';

    const selectorStyle: any = {};
    if (isFailed) selectorStyle.color = 'red';
    if (isLoading) selectorStyle.color = '#aaa';

    const selectApis = this.state.apis
      .slice()
      .filter(api => !!api.id)
      .sort()
      .map(api => ({ label: api.id, value: api.id, clearableValue: false }));

    return (
      <div className="topbar">
        <div className="wrapper">
          <div className="topbar-wrapper">
            <Link href="#" title="API Discovery">
              <img height="30" width="30" src={Logo} alt="Swagger UX" />
              <span>API Discovery</span>
            </Link>
            <div className="download-url-wrapper">
              <Select
                style={selectorStyle}
                name="select-api"
                value={this.state.selectedApiId}
                options={selectApis}
                clearable={false}
                onChange={this.onApiSelected.bind(this)}
              />
            </div>
            &nbsp;
            <Button className="btn execute" onClick={this.props.apiDiscoveryActions.fetchToken}>
              Login
            </Button>
          </div>
        </div>
      </div>
    );
  }
}
