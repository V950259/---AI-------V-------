import { shallowMount, createLocalVue } from '@vue/test-utils'
import ElementUI from 'element-ui'
import Dept from '@/views/system/dept/index.vue'
import store from '@/store'
import router from '@/router'

const localVue = createLocalVue()
localVue.use(ElementUI)

describe('Dept Management', () => {
  let wrapper

  beforeEach(() => {
    wrapper = shallowMount(Dept, {
      localVue,
      store,
      router
    })
  })

  afterEach(() => {
    wrapper.destroy()
  })

  it('正常场景：部门管理页面渲染', () => {
    expect(wrapper.exists()).toBe(true)
  })

  it('正常场景：部门树数据', () => {
    const deptTree = [
      { deptId: 100, deptName: '总公司', children: [] }
    ]
    wrapper.setData({ deptTree })
    expect(wrapper.vm.deptTree.length).toBe(1)
  })

  it('边界场景：空部门树', () => {
    wrapper.setData({ deptTree: [] })
    expect(wrapper.vm.deptTree.length).toBe(0)
  })
})

