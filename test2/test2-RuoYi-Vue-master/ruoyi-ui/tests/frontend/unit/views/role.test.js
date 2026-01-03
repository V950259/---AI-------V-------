import { shallowMount, createLocalVue } from '@vue/test-utils'
import ElementUI from 'element-ui'
import Role from '@/views/system/role/index.vue'
import store from '@/store'
import router from '@/router'

const localVue = createLocalVue()
localVue.use(ElementUI)

describe('Role Management', () => {
  let wrapper

  beforeEach(() => {
    wrapper = shallowMount(Role, {
      localVue,
      store,
      router
    })
  })

  afterEach(() => {
    wrapper.destroy()
  })

  it('正常场景：角色管理页面渲染', () => {
    expect(wrapper.exists()).toBe(true)
  })

  it('正常场景：角色列表数据', () => {
    wrapper.setData({
      roleList: [
        { roleId: 1, roleName: '管理员', roleKey: 'admin' },
        { roleId: 2, roleName: '普通用户', roleKey: 'user' }
      ]
    })
    expect(wrapper.vm.roleList.length).toBe(2)
  })

  it('边界场景：空角色列表', () => {
    wrapper.setData({ roleList: [] })
    expect(wrapper.vm.roleList.length).toBe(0)
  })
})

