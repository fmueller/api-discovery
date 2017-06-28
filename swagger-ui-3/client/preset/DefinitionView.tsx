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
  apiPortalActions: object;
  apiPortalSelectors: any;
};

/**
 * Based on the BaseLayout.
 * See https://github.com/swagger-api/swagger-ui/blob/master/src/core/components/layouts/base.jsx
 */
export default class DefinitionView extends React.Component<Props, undefined> {
  public static readonly propTypes = {
    errSelectors: PropTypes.object.isRequired,
    errActions: PropTypes.object.isRequired,
    specActions: PropTypes.object.isRequired,
    specSelectors: PropTypes.object.isRequired,
    layoutSelectors: PropTypes.object.isRequired,
    layoutActions: PropTypes.object.isRequired,
    getComponent: PropTypes.func.isRequired,
    apiPortalActions: PropTypes.object.isRequired,
    apiPortalSelectors: PropTypes.object.isRequired
  };

  public render() {
    const { getComponent, apiPortalSelectors } = this.props;

    const DefinitionInfo = getComponent('DefinitionInfo', true);
    const Operations = getComponent('operations', true);
    const Models = getComponent('models', true);
    const Row = getComponent('Row');
    const Col = getComponent('Col');
    const Errors = getComponent('errors', true);

    const apiVersions = apiPortalSelectors.apiVersions();

    if (!apiVersions.length) {
      return (
        <div className="info">
          <h4>No API selected.</h4>
        </div>
      );
    }

    return (
      <div className="swagger-ui">
        <div>
          <Errors />
          <Row className="information-container">
            <Col mobile={12} desktop={12}>
              <DefinitionInfo />
            </Col>
          </Row>
          <Row>
            <Col mobile={12} desktop={12}>
              <Operations />
            </Col>
          </Row>
          <Row>
            <Col mobile={12} desktop={12}>
              <Models />
            </Col>
          </Row>
        </div>
      </div>
    );
  }
}
