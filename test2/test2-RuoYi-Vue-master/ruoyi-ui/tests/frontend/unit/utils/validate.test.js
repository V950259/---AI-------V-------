import { validUsername, validEmail, validURL, isEmpty, isHttp, isExternal } from '@/utils/validate'

describe('表单验证工具函数', () => {
  describe('validUsername', () => {
    it('正常场景：有效用户名', () => {
      expect(validUsername('admin')).toBe(true)
      expect(validUsername('editor')).toBe(true)
    })

    it('边界场景：无效用户名', () => {
      expect(validUsername('user123')).toBe(false)
      expect(validUsername('test')).toBe(false)
    })

    it('边界场景：空用户名', () => {
      expect(validUsername('')).toBe(false)
    })
  })

  describe('validEmail', () => {
    it('正常场景：有效邮箱', () => {
      expect(validEmail('test@example.com')).toBe(true)
      expect(validEmail('user@test.cn')).toBe(true)
    })

    it('边界场景：无效邮箱格式', () => {
      expect(validEmail('invalid-email')).toBe(false)
      expect(validEmail('test@')).toBe(false)
    })

    it('边界场景：空邮箱', () => {
      expect(validEmail('')).toBe(false)
    })
  })

  describe('validURL', () => {
    it('正常场景：有效URL', () => {
      expect(validURL('http://www.example.com')).toBe(true)
      expect(validURL('https://www.example.com')).toBe(true)
    })

    it('边界场景：无效URL格式', () => {
      expect(validURL('not-a-url')).toBe(false)
      expect(validURL('www.example.com')).toBe(false)
    })
  })

  describe('isEmpty', () => {
    it('正常场景：非空值', () => {
      expect(isEmpty('test')).toBe(false)
      expect(isEmpty('123')).toBe(false)
    })

    it('边界场景：空值', () => {
      expect(isEmpty('')).toBe(true)
      expect(isEmpty(null)).toBe(true)
      expect(isEmpty(undefined)).toBe(true)
    })
  })

  describe('isHttp', () => {
    it('正常场景：HTTP/HTTPS URL', () => {
      expect(isHttp('http://www.example.com')).toBe(true)
      expect(isHttp('https://www.example.com')).toBe(true)
    })

    it('边界场景：非HTTP URL', () => {
      expect(isHttp('ftp://example.com')).toBe(false)
      expect(isHttp('www.example.com')).toBe(false)
    })
  })

  describe('isExternal', () => {
    it('正常场景：外链', () => {
      expect(isExternal('https://www.example.com')).toBe(true)
      expect(isExternal('mailto:test@example.com')).toBe(true)
      expect(isExternal('tel:1234567890')).toBe(true)
    })

    it('边界场景：内部链接', () => {
      expect(isExternal('/user')).toBe(false)
      expect(isExternal('user')).toBe(false)
    })
  })
})

