import { shallowMount, createLocalVue } from '@vue/test-utils'
import ElementUI from 'element-ui'
import Pagination from '@/components/Pagination/index.vue'

const localVue = createLocalVue()
localVue.use(ElementUI)

describe('Pagination.vue', () => {
  let wrapper

  beforeEach(() => {
    wrapper = shallowMount(Pagination, {
      localVue,
      propsData: {
        total: 100,
        page: 1,
        limit: 20
      }
    })
  })

  afterEach(() => {
    wrapper.destroy()
  })

  it('正常场景：分页组件渲染', () => {
    expect(wrapper.find('.pagination-container').exists()).toBe(true)
  })

  it('正常场景：显示总记录数', () => {
    expect(wrapper.props('total')).toBe(100)
  })

  it('正常场景：显示当前页码', () => {
    expect(wrapper.props('page')).toBe(1)
  })

  it('正常场景：显示每页条数', () => {
    expect(wrapper.props('limit')).toBe(20)
  })

  it('边界场景：总记录数为0', () => {
    wrapper.setProps({ total: 0 })
    expect(wrapper.props('total')).toBe(0)
  })

  it('边界场景：总记录数很大', () => {
    wrapper.setProps({ total: 999999 })
    expect(wrapper.props('total')).toBe(999999)
  })

  it('边界场景：每页条数为1', () => {
    wrapper.setProps({ limit: 1 })
    expect(wrapper.props('limit')).toBe(1)
  })

  it('边界场景：每页条数很大', () => {
    wrapper.setProps({ limit: 1000 })
    expect(wrapper.props('limit')).toBe(1000)
  })

  it('正常场景：页码变化事件', async () => {
    wrapper.vm.handleCurrentChange(2)
    await wrapper.vm.$nextTick()
    expect(wrapper.emitted('pagination')).toBeTruthy()
  })

  it('正常场景：每页条数变化事件', async () => {
    wrapper.vm.handleSizeChange(50)
    await wrapper.vm.$nextTick()
    expect(wrapper.emitted('pagination')).toBeTruthy()
  })

  it('边界场景：页码超出范围', () => {
    wrapper.setProps({ page: 0 })
    expect(wrapper.props('page')).toBe(0)
  })
})

