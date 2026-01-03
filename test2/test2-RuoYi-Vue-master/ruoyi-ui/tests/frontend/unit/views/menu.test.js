import { shallowMount, createLocalVue } from '@vue/test-utils'
import ElementUI from 'element-ui'
import Menu from '@/views/system/menu/index.vue'
import store from '@/store'
import router from '@/router'

const localVue = createLocalVue()
localVue.use(ElementUI)

describe('Menu Management', () => {
  let wrapper

  beforeEach(() => {
    wrapper = shallowMount(Menu, {
      localVue,
      store,
      router
    })
  })

  afterEach(() => {
    wrapper.destroy()
  })

  it('正常场景：菜单管理页面渲染', () => {
    expect(wrapper.exists()).toBe(true)
  })

  it('正常场景：菜单树数据', () => {
    const menuTree = [
      { menuId: 1, menuName: '系统管理', children: [] }
    ]
    wrapper.setData({ menuTree })
    expect(wrapper.vm.menuTree.length).toBe(1)
  })

  it('边界场景：空菜单树', () => {
    wrapper.setData({ menuTree: [] })
    expect(wrapper.vm.menuTree.length).toBe(0)
  })
})

