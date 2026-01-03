import { shallowMount, createLocalVue } from '@vue/test-utils'
import ElementUI from 'element-ui'
import Breadcrumb from '@/components/Breadcrumb/index.vue'

const localVue = createLocalVue()
localVue.use(ElementUI)

describe('Breadcrumb.vue', () => {
  let wrapper

  beforeEach(() => {
    wrapper = shallowMount(Breadcrumb, {
      localVue
    })
  })

  afterEach(() => {
    wrapper.destroy()
  })

  it('正常场景：面包屑组件渲染', () => {
    expect(wrapper.find('.app-breadcrumb').exists()).toBe(true)
  })

  it('边界场景：空面包屑数据', () => {
    wrapper.setData({ levelList: [] })
    expect(wrapper.vm.levelList.length).toBe(0)
  })

  it('正常场景：有面包屑数据', () => {
    const levelList = [
      { path: '/', meta: { title: '首页' } },
      { path: '/user', meta: { title: '用户管理' } }
    ]
    wrapper.setData({ levelList })
    expect(wrapper.vm.levelList.length).toBe(2)
  })
})

