import React = require('react');
import PropTypes = require('prop-types');
import Select = require('react-select');
import log from '../framework/debug';
import Logo = require('../img/swagger.png');

type TopbarProps = {
  specSelectors: any;
  specActions: any;
  apiDiscoverySelectors: any;
  getComponent: (name: string, container?: boolean | 'root') => React.ComponentClass<any>;
  apiDiscoveryActions: { [name: string]: (...args: any[]) => void };
};

type TopbarState = {
  url: string;
  apiList: any[];
  selectedApiId?: string;
};

/**
 * Based on the TopBar plugin.
 * See https://github.com/swagger-api/swagger-ui/tree/master/src/plugins/topbar
 *
 * Copyright 2017 SmartBear Software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at [apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
export default () => ({
  components: {
    Topbar
  }
});

class Topbar extends React.Component<TopbarProps, TopbarState> {
  public static readonly propTypes = {
    specSelectors: PropTypes.object.isRequired,
    apiDiscoverySelectors: PropTypes.object.isRequired,
    specActions: PropTypes.object.isRequired,
    getComponent: PropTypes.func.isRequired,
    apiDiscoveryActions: PropTypes.object.isRequired
  };

  constructor(props: TopbarProps, context: any) {
    super(props, context);
    this.state = {
      url: props.specSelectors.url(),
      apiList: props.apiDiscoverySelectors.apiList()
    };
  }

  public componentWillReceiveProps(nextProps: TopbarProps) {
    this.setState({
      url: nextProps.specSelectors.url(),
      apiList: nextProps.apiDiscoverySelectors.apiList()
    });
  }

  // private onUrlChange(e: React.FormEvent<HTMLInputElement>) {
  //   const { currentTarget: { value } } = e;
  //   this.setState({ url: value });
  // }

  private onApiSelected(api: { label: string; value: string }) {
    this.setState({ selectedApiId: api.value });
    this.props.apiDiscoveryActions.fetchApi(api.value);
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

    const selectApis = this.state.apiList
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
            {/*
            <div className="download-url-wrapper">
              <input
                className="download-url-input"
                type="text"
                onChange={this.onUrlChange.bind(this)}
                value={this.state.url}
                disabled={isLoading}
                style={inputStyle}
              />
              <Button className="download-url-button" onClick={this.downloadUrl.bind(this)}>
                Discover
              </Button>
            </div>
            */}
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
