import tv4 = require('tv4');

class ValidationError extends Error {
  constructor(error: tv4.ValidationError) {
    super(error.message);
  }
}

export default function validate(data: any, schema: tv4.JsonSchema) {
  const result = tv4.validateResult(data, schema);
  if (!result.valid) throw new ValidationError(result.error);
  else return data;
}

export abstract class Validated {
  constructor(data: any, schema: tv4.JsonSchema) {
    Object.assign(this, validate(data, schema));
  }
}
