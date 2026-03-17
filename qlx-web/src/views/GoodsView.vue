<template>
  <div>
    <el-card>
      <div style="margin-bottom: 20px; display: flex; gap: 10px; align-items: center; flex-wrap: wrap;">
        <el-input 
          v-model="searchKeyword" 
          placeholder="物品名称或备注..." 
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

        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button type="warning" @click="resetParam">重置</el-button>
        <el-button type="success" @click="handleAdd" style="margin-left: auto;">
          <el-icon style="margin-right: 5px;"><Plus /></el-icon> 新增物品
        </el-button>
      </div>
      
      <el-table :data="tableData" border stripe :header-cell-style="{ background: '#f4f6f8', color: '#333' }">
        <el-table-column prop="id" label="物品ID" width="80" align="center" />
        <el-table-column prop="name" label="物品名称" min-width="150" />
        <el-table-column prop="storage" label="所属仓库" min-width="120" :formatter="formatStorage" />
        <el-table-column prop="goodsType" label="物品分类" min-width="120" :formatter="formatGoodsType" />
        <el-table-column prop="count" label="当前库存" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.count > 10 ? 'success' : 'danger'">{{ scope.row.count }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="180" show-overflow-tooltip />
        <el-table-column label="操作" width="180" fixed="right" align="center">
          <template #default="scope">
            <el-button size="small" type="primary" plain @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" plain @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div style="margin-top: 20px; display: flex; justify-content: flex-end;">
        <el-pagination
          v-model:current-page="pageNum" v-model:page-size="pageSize" :page-sizes="[5, 10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper" :total="total"
          @size-change="handleSizeChange" @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑物品' : '新增物品'" width="35%" :before-close="handleClose">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="物品名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入物品名称" />
        </el-form-item>
        <el-form-item label="所属仓库" prop="storage">
          <el-select v-model="form.storage" placeholder="请选择存放仓库" style="width: 100%;">
            <el-option v-for="item in storageData" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="物品分类" prop="goodsType">
          <el-select v-model="form.goodsType" placeholder="请选择物品分类" style="width: 100%;">
            <el-option v-for="item in goodsTypeData" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="初始库存" prop="count">
          <el-input-number v-model="form.count" :min="0" :max="99999" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="备注信息" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="选填..." />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveGoods">确认保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import request from '../utils/request.js' 

const tableData = ref([]) 
const pageNum = ref(1)    
const pageSize = ref(10)  
const total = ref(0)      

// 🌟 新增：独立出三个搜索变量，防止和表单的变量混淆
const searchKeyword = ref('')   
const searchStorage = ref('')
const searchGoodsType = ref('')

const storageData = ref([])
const goodsTypeData = ref([])

const loadDictionaries = () => {
  request.get('/storage/list').then(res => { storageData.value = res.data || res || [] })
  request.get('/goodstype/list').then(res => { goodsTypeData.value = res.data || res || [] })
}

const loadData = () => {
  const queryParam = {
    pageNum: pageNum.value, 
    pageSize: pageSize.value, 
    param: { 
      keyword: searchKeyword.value,
      storage: searchStorage.value,      // 🌟 把选择的仓库ID发给后端
      goodsType: searchGoodsType.value   // 🌟 把选择的分类ID发给后端
    }
  }
  request.post('/goods/list/page', queryParam).then(res => {
    tableData.value = res.records || [] 
    total.value = res.total || 0        
  }).catch(() => { ElMessage.error("获取物品列表失败") })
}

const formatStorage = (row) => {
  const temp = storageData.value.find(v => v.id === row.storage)
  return temp ? temp.name : '未知仓库'
}

const formatGoodsType = (row) => {
  const temp = goodsTypeData.value.find(v => v.id === row.goodsType)
  return temp ? temp.name : '未知分类'
}

// 🌟 升级版重置：要把下拉框的值也清空
const resetParam = () => { 
  searchKeyword.value = ''
  searchStorage.value = ''
  searchGoodsType.value = ''
  pageNum.value = 1
  loadData() 
}

const handleSizeChange = (val) => { pageSize.value = val; pageNum.value = 1; loadData() }
const handleCurrentChange = (val) => { pageNum.value = val; loadData() }

// ====== 表单操作逻辑 ======
const dialogVisible = ref(false) 
const formRef = ref(null)
const form = ref({ id: '', name: '', storage: '', goodsType: '', count: 0, remark: '' })

const rules = {
  name: [{ required: true, message: '物品名称不能为空', trigger: 'blur' }],
  storage: [{ required: true, message: '必须选择一个所属仓库', trigger: 'change' }],
  goodsType: [{ required: true, message: '必须选择一个物品分类', trigger: 'change' }]
}

const handleAdd = () => {
  form.value = { id: '', name: '', storage: '', goodsType: '', count: 0, remark: '' }
  dialogVisible.value = true 
  if (formRef.value) formRef.value.clearValidate()
}

const handleEdit = (row) => {
  form.value = JSON.parse(JSON.stringify(row)) 
  dialogVisible.value = true
  if (formRef.value) formRef.value.clearValidate()
}

const handleClose = (done) => { done() }

const saveGoods = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      const url = form.value.id ? '/goods/update' : '/goods/add'
      
      // 🌟 修复假报错：去掉多余的 res.code 判断，因为能走进 then 就代表后端已经 save 成功了！
      request.post(url, form.value).then(() => {
        ElMessage.success(form.value.id ? '物品修改成功！' : '物品新增成功！')
        dialogVisible.value = false // 关闭弹窗
        loadData() // 重新拉取最新数据（不用你手动刷新网页了）
      })
      
    } else {
      ElMessage.warning('请检查表单中标红的错误！')
      return false
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除物品【${row.name}】吗？`, '警告', { type: 'warning' }).then(() => {
    request.delete('/goods/' + row.id).then(() => {
      ElMessage.success('删除成功！')
      loadData() 
    })
  })
}

onMounted(() => {
  loadDictionaries()
  loadData()
})
</script>