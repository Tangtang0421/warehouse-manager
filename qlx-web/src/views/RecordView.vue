<template>
  <div>
    <el-card>
      <div style="margin-bottom: 20px; display: flex; gap: 10px; align-items: center; flex-wrap: wrap;">
        <el-input 
          v-model="searchGoodsName" 
          placeholder="搜索物品名称..." 
          style="width: 200px" 
          clearable 
          @keyup.enter="loadData"
        >
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        
        <el-select v-model="searchStorage" placeholder="请选择仓库" clearable style="width: 150px;">
          <el-option v-for="item in storageData" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>

        <el-select v-model="searchGoodsType" placeholder="请选择分类" clearable style="width: 150px;">
          <el-option v-for="item in goodsTypeData" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>

        <el-button type="primary" @click="loadData">查询流水</el-button>
        <el-button type="warning" @click="resetParam">重置条件</el-button>
      </div>
      
      <el-table 
        :data="tableData" 
        border 
        stripe 
        :header-cell-style="{ background: '#f4f6f8', color: '#333' }"
      >
        <el-table-column prop="id" label="流水单号" width="100" align="center" />
        
        <el-table-column prop="goodsName" label="操作物品" min-width="150">
          <template #default="scope">
            <span style="font-weight: bold; color: #409EFF;">{{ scope.row.goodsName }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="storage" label="所属仓库" min-width="120" :formatter="formatStorage" />
        <el-table-column prop="goodsType" label="物品分类" min-width="120" :formatter="formatGoodsType" />
        
        <el-table-column prop="count" label="操作数量" width="120" align="center">
          <template #default="scope">
             <el-tag effect="dark" type="info">{{ scope.row.count }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="userName" label="办理人(业务员)" min-width="120" align="center" />
        <el-table-column prop="adminName" label="系统录入人" min-width="120" align="center" />
        
        <el-table-column prop="createtime" label="操作时间" min-width="180" align="center" />
        <el-table-column prop="remark" label="单据备注" min-width="180" show-overflow-tooltip />
      </el-table>

      <div style="margin-top: 20px; display: flex; justify-content: flex-end;">
        <el-pagination
          v-model:current-page="pageNum" 
          v-model:page-size="pageSize" 
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper" 
          :total="total"
          @size-change="handleSizeChange" 
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request.js' 

// ====== 状态变量 ======
const tableData = ref([]) 
const pageNum = ref(1)    
const pageSize = ref(10)  
const total = ref(0)      

const searchGoodsName = ref('')   
const searchStorage = ref('')
const searchGoodsType = ref('')

const storageData = ref([])
const goodsTypeData = ref([])

const loadDictionaries = () => {
  request.get('/storage/list').then(res => { storageData.value = res.data || res || [] })
  request.get('/goodstype/list').then(res => { goodsTypeData.value = res.data || res || [] })
}

// 🌟 核心修改：动态带上用户身份
const loadData = () => {
  // 1. 从浏览器缓存获取当前登录人信息
  const userStr = localStorage.getItem('user') || '{}'
  const currentUser = JSON.parse(userStr)

  const queryParam = {
    pageNum: pageNum.value, 
    pageSize: pageSize.value, 
    goodsName: searchGoodsName.value, 
    storage: searchStorage.value, 
    goodsType: searchGoodsType.value,
    userId: currentUser.id,
    roleId: currentUser.roleId
  }
  
  request.post('/record/list/page', queryParam).then(res => {
    tableData.value = res.records || [] 
    total.value = res.total || 0        
  }).catch(() => {
    ElMessage.error('获取流水记录失败！')
  })
}

const formatStorage = (row) => {
  const temp = storageData.value.find(v => v.id === row.storage)
  return temp ? temp.name : '未知'
}

const formatGoodsType = (row) => {
  const temp = goodsTypeData.value.find(v => v.id === row.goodsType)
  return temp ? temp.name : '未知'
}

const resetParam = () => { 
  searchGoodsName.value = ''
  searchStorage.value = ''
  searchGoodsType.value = ''
  pageNum.value = 1
  loadData() 
}

const handleSizeChange = (val) => { pageSize.value = val; pageNum.value = 1; loadData() }
const handleCurrentChange = (val) => { pageNum.value = val; loadData() }

onMounted(() => {
  loadDictionaries()
  loadData()
})
</script>

<style scoped>
/* 可根据需要添加样式 */
</style>