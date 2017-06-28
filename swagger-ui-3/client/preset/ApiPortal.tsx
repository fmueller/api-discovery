import React = require('react');
import PropTypes = require('prop-types');

type Props = {
  errSelectors: object;
  errActions: object;
  specActions: object;
  specSelectors: any;
  layoutSelectors: object;
  layoutActions: object;
  getComponent: (name: string, container?: boolean | 'root') => React.ComponentType<any>;
};

/**
 * Based on the Standalone preset.
 * See https://github.com/swagger-api/swagger-ui/tree/master/src/standalone
 */
export default class ApiPortal extends React.Component<Props, undefined> {
  public static readonly propTypes = {
    errSelectors: PropTypes.object.isRequired,
    errActions: PropTypes.object.isRequired,
    specActions: PropTypes.object.isRequired,
    specSelectors: PropTypes.object.isRequired,
    layoutSelectors: PropTypes.object.isRequired,
    layoutActions: PropTypes.object.isRequired,
    getComponent: PropTypes.func.isRequired
  };

  public render() {
    const { getComponent, specSelectors } = this.props;

    const Container = getComponent('Container');
    const Topbar = getComponent('Topbar', true);
    const DefinitionView = getComponent('DefinitionView', true);

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
        {loadingStatus !== 'loading' ? <DefinitionView /> : null}
      </Container>
    );
  }
}
