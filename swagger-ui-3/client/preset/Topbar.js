import React, { Component } from 'react';
import PropTypes from 'prop-types';
import Logo from '../img/swagger.png';

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

class Topbar extends Component {
  static propTypes = {
    specSelectors: PropTypes.object.isRequired,
    specActions: PropTypes.object.isRequired,
    getComponent: PropTypes.func.isRequired
  };

  constructor(props, context) {
    super(props, context);
    this.state = { url: props.specSelectors.url() };
  }

  componentWillReceiveProps(nextProps) {
    this.setState({ url: nextProps.specSelectors.url() });
  }

  onUrlChange = e => {
    const { target: { value } } = e;
    this.setState({ url: value });
  };

  downloadUrl = () => {
    this.props.apiDiscoveryActions.fetchApi(this.state.url);
  };

  componentDidMount() {
    console.log('Topbar props', this.props);
  }

  render() {
    const { getComponent, specSelectors } = this.props;
    const Button = getComponent('Button');
    const Link = getComponent('Link');

    const isLoading = specSelectors.loadingStatus() === 'loading';
    const isFailed = specSelectors.loadingStatus() === 'failed';

    const inputStyle = {};
    if (isFailed) inputStyle.color = 'red';
    if (isLoading) inputStyle.color = '#aaa';
    return (
      <div className="topbar">
        <div className="wrapper">
          <div className="topbar-wrapper">
            <Link href="#" title="API Discovery">
              <img height="30" width="30" src={Logo} alt="Swagger UX" />
              <span>API Discovery</span>
            </Link>
            <div className="download-url-wrapper">
              <input
                className="download-url-input"
                type="text"
                onChange={this.onUrlChange}
                value={this.state.url}
                disabled={isLoading}
                style={inputStyle}
              />
              <Button className="download-url-button" onClick={this.downloadUrl}>Discover</Button>
            </div>
          </div>
        </div>
      </div>
    );
  }
}
