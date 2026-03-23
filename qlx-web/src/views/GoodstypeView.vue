<template>
  <div>
    <el-card>
      <div style="margin-bottom: 20px; display: flex; gap: 10px; align-items: center;">
        <el-input 
          v-model="keyword" 
          placeholder="请输入分类名称或备注进行检索..." 
          style="width: 250px" 
          clearable 
          @keyup.enter="loadData"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button type="warning" @click="resetParam">重置</el-button>
        <el-button type="success" @click="handleAdd" style="margin-left: auto;">
          <el-icon style="margin-right: 5px;"><Plus /></el-icon> 新增分类
        </el-button>
      </div>
      
      <el-table 
        :data="tableData" 
        style="width: 100%;" 
        border
        stripe
        :header-cell-style="{ background: '#f4f6f8', color: '#333', fontWeight: 'bold' }"
      >
        <el-table-column prop="id" label="分类ID" width="100" align="center" />
        <el-table-column prop="name" label="分类名称" min-width="150" />
        <el-table-column prop="remark" label="备注信息" min-width="250" show-overflow-tooltip />
        
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template #default="scope">
            <el-button size="small" type="primary" plain @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" plain @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div style="margin-top: 20px; display: flex; justify-content: flex-end;">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :page-sizes="[5, 10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑物品分类' : '新增物品分类'" width="30%" :before-close="handleClose">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称（如：电子产品）" />
        </el-form-item>
        <el-form-item label="备注信息" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入该分类的详细描述..." />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveGoodstype">确认保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import request from '../utils/request.js' 

// ====== 分页与检索变量 ======
const tableData = ref([]) 
const pageNum = ref(1)    
const pageSize = ref(10)  
const total = ref(0)      
const keyword = ref('')   

// 获取表格数据
const loadData = () => {
  const queryParam = {
    pageNum: pageNum.value,
    pageSize: pageSize.value,
    keyword: keyword.value
  }
  // 🌟 调用物品分类的分页接口
  request.post('/goodstype/list/page', queryParam).then(res => {
    tableData.value = res.records || [] 
    total.value = res.total || 0        
  }).catch(() => {
    ElMessage.error("获取物品分类列表失败")
  })
}

const resetParam = () => {
  keyword.value = ''
  pageNum.value = 1
  loadData()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  pageNum.value = 1 
  loadData()
}

const handleCurrentChange = (val) => {
  pageNum.value = val
  loadData()
}

// ====== 表单操作逻辑 ======
const dialogVisible = ref(false) 
const formRef = ref(null)

const form = ref({ id: '', name: '', remark: '' })

// 🌟 严谨的前端表单校验
const rules = {
  name: [
    { required: true, message: '分类名称不能为空', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ]
}

const handleAdd = () => {
  form.value = { id: '', name: '', remark: '' }
  dialogVisible.value = true 
  if (formRef.value) formRef.value.clearValidate()
}

const handleEdit = (row) => {
  form.value = JSON.parse(JSON.stringify(row)) 
  dialogVisible.value = true
  if (formRef.value) formRef.value.clearValidate()
}

const handleClose = (done) => { done() }

const saveGoodstype = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      // 🌟 根据是否有 id 决定是新增还是修改
      const url = form.value.id ? '/goodstype/update' : '/goodstype/add'
      request.post(url, form.value).then(() => {
        ElMessage.success(form.value.id ? '分类修改成功！' : '分类新增成功！')
        dialogVisible.value = false
        loadData() 
      })
    } else {
      ElMessage.warning('请检查表单中标红的错误！')
      return false
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`您确定要删除分类【${row.name}】吗？`, '高危操作警告', {
    confirmButtonText: '确定删除', cancelButtonText: '取消', type: 'warning',
  }).then(() => {
    // 🌟 调用删除接口
    request.delete('/goodstype/' + row.id).then(() => {
      ElMessage.success('删除成功！')
      loadData() 
    })
  }).catch(() => {
    ElMessage.info('已取消删除')
  })
}

// 初始化加载数据
loadData() 
</script>

<style scoped>
</style>