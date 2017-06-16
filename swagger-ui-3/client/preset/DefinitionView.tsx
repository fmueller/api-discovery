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
  apiDiscoveryActions: object;
  apiDiscoverySelectors: any;
};

type State = {
  selectedApiVersion: ApiVersion;
};

/**
 * Based on the BaseLayout.
 * See https://github.com/swagger-api/swagger-ui/blob/master/src/core/components/layouts/base.jsx
 */
export default class DefinitionView extends React.Component<Props, State> {
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

  public render() {
    const { specSelectors, getComponent, apiDiscoverySelectors } = this.props;

    const info = specSelectors.info();
    const url = specSelectors.url();
    const basePath = specSelectors.basePath();
    const host = specSelectors.host();
    const externalDocs = specSelectors.externalDocs();

    const Info = getComponent('info');
    const Operations = getComponent('operations', true);
    const Models = getComponent('models', true);
    const Row = getComponent('Row');
    const Col = getComponent('Col');
    const Errors = getComponent('errors', true);
    const DefinitionMenu = getComponent('DefinitionMenu', true);

    const apiVersions = apiDiscoverySelectors.apiVersions();

    if (!apiVersions.length) {
      return <h4>No API selected.</h4>;
    }

    return (
      <div className="swagger-ui">
        <div>
          <Errors />
          <Row className="information-container">
            <Col mobile={6} desktop={6}>
              {info.count()
                ? <Info
                    info={info}
                    url={url}
                    host={host}
                    basePath={basePath}
                    externalDocs={externalDocs}
                    getComponent={getComponent}
                  />
                : null}
            </Col>
            <Col mobile={6} desktop={6}>
              <DefinitionMenu />
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
