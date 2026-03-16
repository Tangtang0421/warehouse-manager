<template>
  <div>
    <el-card>
      <div style="margin-bottom: 20px; display: flex; gap: 10px; align-items: center;">
        <el-input 
          v-model="keyword" 
          placeholder="请输入仓库名称进行模糊检索..." 
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
          <el-icon style="margin-right: 5px;"><Plus /></el-icon> 新增仓库
        </el-button>
      </div>
      
      <el-table 
        :data="tableData" 
        style="width: 100%;" 
        border
        stripe
        :header-cell-style="{ background: '#f4f6f8', color: '#333', fontWeight: 'bold' }"
      >
        <el-table-column prop="id" label="仓库ID" width="100" align="center" />
        <el-table-column prop="name" label="仓库名称" min-width="150" />
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

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑仓库信息' : '新增仓库'" width="30%" :before-close="handleClose">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="仓库名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入仓库名称（如：北京一号仓）" />
        </el-form-item>
        <el-form-item label="备注信息" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入仓库的详细地址或描述信息..." />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveStorage">确认保存</el-button>
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

// ====== 🌟 分页与检索核心变量 ======
const tableData = ref([]) 
const pageNum = ref(1)    // 当前页码
const pageSize = ref(10)  // 每页条数
const total = ref(0)      // 总条数
const keyword = ref('')   // 搜索关键字

// 🌟 核心：向后端发送分页请求
const loadData = () => {
  const queryParam = {
    pageNum: pageNum.value,
    pageSize: pageSize.value,
    param: {
      keyword: keyword.value
    }
  }
  // 注意这里改成了 POST 请求，路径也改成了 /list/page
  request.post('/storage/list/page', queryParam).then(res => {
    tableData.value = res.records || [] // 拿到当前页的数据
    total.value = res.total || 0        // 拿到数据库里的总条数
  }).catch(() => {
    ElMessage.error("获取仓库列表失败")
  })
}

// 重置搜索条件
const resetParam = () => {
  keyword.value = ''
  pageNum.value = 1
  loadData()
}

// 切换每页显示多少条
const handleSizeChange = (val) => {
  pageSize.value = val
  pageNum.value = 1 // 切换每页大小时，回到第一页
  loadData()
}

// 点击页码跳转
const handleCurrentChange = (val) => {
  pageNum.value = val
  loadData()
}

// ====== 表单与操作逻辑（除了调用 loadData 外，其余基本不变） ======
const dialogVisible = ref(false) 
const formRef = ref(null)

const form = ref({ id: '', name: '', remark: '' })

const rules = {
  name: [
    { required: true, message: '仓库名称不能为空', trigger: 'blur' },
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

const saveStorage = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      const url = form.value.id ? '/storage/update' : '/storage/add'
      request.post(url, form.value).then(() => {
        ElMessage.success(form.value.id ? '仓库修改成功！' : '仓库新增成功！')
        dialogVisible.value = false
        loadData() // 🌟 保存成功后重新拉取当页数据
      })
    } else {
      ElMessage.warning('请检查表单中标红的错误！')
      return false
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`您确定要删除仓库【${row.name}】吗？删除后不可恢复！`, '高危操作警告', {
    confirmButtonText: '确定删除', cancelButtonText: '取消', type: 'warning',
  }).then(() => {
    request.delete('/storage/' + row.id).then(() => {
      ElMessage.success('删除成功！')
      loadData() // 🌟 删除成功后重新拉取当页数据
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