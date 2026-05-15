import { describe, expect, it } from 'vitest';
import { multipartRequestConfig } from './api';

describe('api request configuration', () => {
  it('lets the browser set multipart boundaries for case submissions', () => {
    expect(multipartRequestConfig()).toEqual({});
  });
});
