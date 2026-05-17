import { describe, expect, it } from 'vitest';
import { apiBaseUrl, attachmentDownloadUrl, multipartRequestConfig } from './api';

describe('api request configuration', () => {
  it('uses relative API paths by default for local proxy deployments', () => {
    expect(apiBaseUrl).toBe('/api');
    expect(attachmentDownloadUrl(12, 34)).toBe('/api/cases/12/attachments/34/download');
  });

  it('lets the browser set multipart boundaries for case submissions', () => {
    expect(multipartRequestConfig()).toEqual({});
  });
});
