// Type definitions for swagger-ui 3.x
// Project: https://github.com/swagger-api/swagger-ui
// Definitions by: mfellner <https://github.com/mfellner>

declare module 'swagger-ui' {
  export = swaggerUi;

  const swaggerUi: SwaggerUI;

  interface SwaggerUI extends Function {
    (options: any): System;
    presets: { apis: any };
    plugins: { [name: string]: any };
  }

  interface System {}
}
