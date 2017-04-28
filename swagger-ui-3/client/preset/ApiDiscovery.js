import React, { Component } from 'react';
import PropTypes from 'prop-types';

/**
 * Based on the Standalone preset.
 * See https://github.com/swagger-api/swagger-ui/tree/master/src/standalone
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
export default class ApiDiscovery extends Component {
  static propTypes = {
    errSelectors: PropTypes.object.isRequired,
    errActions: PropTypes.object.isRequired,
    specActions: PropTypes.object.isRequired,
    specSelectors: PropTypes.object.isRequired,
    layoutSelectors: PropTypes.object.isRequired,
    layoutActions: PropTypes.object.isRequired,
    getComponent: PropTypes.func.isRequired
  };

  render() {
    const { getComponent, specSelectors } = this.props;

    const Container = getComponent('Container');
    const Row = getComponent('Row');
    const Col = getComponent('Col');

    const Topbar = getComponent('Topbar', true);
    const BaseLayout = getComponent('BaseLayout', true);

    const loadingStatus = specSelectors.loadingStatus();

    return (
      <Container className="swagger-ui">
        <Topbar />
        {loadingStatus === 'loading' &&
          <div className="info">
            <h4 className="title">Loading...</h4>
          </div>}
        {loadingStatus === 'failed' &&
          <div className="info">
            <h4 className="title">Failed to load spec.</h4>
          </div>}
        {loadingStatus === 'failedConfig' &&
          <div
            className="info"
            style={{
              maxWidth: '880px',
              marginLeft: 'auto',
              marginRight: 'auto',
              textAlign: 'center'
            }}
          >
            <h4 className="title">Failed to load config.</h4>
          </div>}
        {!loadingStatus || (loadingStatus === 'success' && <BaseLayout />)}
      </Container>
    );
  }
}
