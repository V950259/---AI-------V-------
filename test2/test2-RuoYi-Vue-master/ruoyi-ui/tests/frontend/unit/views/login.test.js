import { shallowMount, createLocalVue } from '@vue/test-utils'
import Vue from 'vue'
import ElementUI from 'element-ui'
import Login from '@/views/login.vue'
import store from '@/store'
import router from '@/router'

const localVue = createLocalVue()
localVue.use(ElementUI)

describe('Login.vue', () => {
  let wrapper

  beforeEach(() => {
    wrapper = shallowMount(Login, {
      localVue,
      store,
      router
    })
  })

  afterEach(() => {
    wrapper.destroy()
  })

  it('正常场景：登录表单渲染', () => {
    expect(wrapper.find('.login-form').exists()).toBe(true)
    expect(wrapper.find('input[type="text"]').exists()).toBe(true)
    expect(wrapper.find('input[type="password"]').exists()).toBe(true)
  })

  it('正常场景：输入用户名和密码', async () => {
    const usernameInput = wrapper.find('input[type="text"]')
    const passwordInput = wrapper.find('input[type="password"]')
    
    await usernameInput.setValue('admin')
    await passwordInput.setValue('admin123')
    
    expect(wrapper.vm.loginForm.username).toBe('admin')
    expect(wrapper.vm.loginForm.password).toBe('admin123')
  })

  it('边界场景：空用户名', async () => {
    const usernameInput = wrapper.find('input[type="text"]')
    await usernameInput.setValue('')
    
    expect(wrapper.vm.loginForm.username).toBe('')
  })

  it('边界场景：空密码', async () => {
    const passwordInput = wrapper.find('input[type="password"]')
    await passwordInput.setValue('')
    
    expect(wrapper.vm.loginForm.password).toBe('')
  })

  it('边界场景：超长用户名', async () => {
    const longUsername = 'a'.repeat(1000)
    const usernameInput = wrapper.find('input[type="text"]')
    await usernameInput.setValue(longUsername)
    
    expect(wrapper.vm.loginForm.username.length).toBe(1000)
  })

  it('边界场景：超长密码', async () => {
    const longPassword = 'a'.repeat(1000)
    const passwordInput = wrapper.find('input[type="password"]')
    await passwordInput.setValue(longPassword)
    
    expect(wrapper.vm.loginForm.password.length).toBe(1000)
  })

  it('正常场景：点击登录按钮', async () => {
    wrapper.vm.loginForm.username = 'admin'
    wrapper.vm.loginForm.password = 'admin123'
    
    const loginButton = wrapper.find('button[type="button"]')
    expect(loginButton.exists()).toBe(true)
  })

  it('边界场景：特殊字符用户名', async () => {
    const specialUsername = 'admin@#$%^&*()'
    const usernameInput = wrapper.find('input[type="text"]')
    await usernameInput.setValue(specialUsername)
    
    expect(wrapper.vm.loginForm.username).toBe(specialUsername)
  })

  it('边界场景：数字用户名', async () => {
    const numericUsername = '123456'
    const usernameInput = wrapper.find('input[type="text"]')
    await usernameInput.setValue(numericUsername)
    
    expect(wrapper.vm.loginForm.username).toBe(numericUsername)
  })
})

