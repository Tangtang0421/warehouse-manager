<template>
  <div>
    <el-card>
      <div style="margin-bottom: 20px; display: flex; gap: 10px; align-items: center;">
        <el-input v-model="keyword" placeholder="请输入管理员名字或账号" style="width: 200px" clearable @keyup.enter="loadData" />
        <el-select v-model="sex" placeholder="请选择性别" style="width: 120px" clearable>
          <el-option label="男" :value="1" />
          <el-option label="女" :value="0" />
        </el-select>
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button type="warning" @click="resetParam">重置</el-button>
        <el-button type="success" @click="handleAdd" style="margin-left: auto;">新增管理员</el-button>
      </div>
      
      <el-table 
        :data="tableData" 
        style="width: 100%;" 
        border
        :header-cell-style="{ background: '#f4f6f8', color: '#333', fontWeight: 'bold' }"
      >
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="no" label="账号" min-width="120" />
        <el-table-column prop="name" label="姓名" min-width="120" />
        <el-table-column prop="phone" label="电话" min-width="150" />
        <el-table-column prop="age" label="年龄" width="80" align="center" />
        
        <el-table-column prop="sex" label="性别" width="80" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.sex === 1 ? 'primary' : 'danger'">
              {{ scope.row.sex === 1 ? '男' : '女' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="roleId" label="角色" width="120" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.roleId === 1" type="primary">管理员</el-tag>
          </template>
        </el-table-column>
        
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

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑管理员' : '新增管理员'" width="30%" :before-close="handleClose">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="账号" prop="no">
          <el-input v-model="form.no" placeholder="请输入账号" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" placeholder="请输入默认密码" show-password />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="年龄" prop="age">
          <el-input v-model="form.age" placeholder="请输入年龄" />
        </el-form-item>
        <el-form-item label="性别" prop="sex">
          <el-select v-model="form.sex" placeholder="请选择性别" style="width: 100%">
            <el-option label="男" :value="1" />
            <el-option label="女" :value="0" />
          </el-select>
        </el-form-item>
        </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveUser">确认保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../utils/request.js' 

const tableData = ref([]) 
const pageNum = ref(1)    
const pageSize = ref(10)  
const total = ref(0)      

const keyword = ref('')
const sex = ref(null)

const loadData = () => {
  const queryParam = {
    pageNum: pageNum.value,  
    pageSize: pageSize.value, 
    keyword: keyword.value,
    sex: sex.value,
    roleId: 1 // 🌟 核心：这里强行传 1，向后端索要管理员数据
  }
  request.post('/user/list/page', queryParam).then(res => {
    tableData.value = res.records || [] 
    total.value = res.total || 0        
  })
}

const resetParam = () => {
  keyword.value = '' 
  sex.value = null     
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

const dialogVisible = ref(false) 
const formRef = ref(null)

const form = ref({
  id: '', no: '', name: '', password: '', phone: '', age: '', sex: null, roleId: null
})

const rules = {
  no: [
    { required: true, message: '账号不能为空', trigger: 'blur' },
    { min: 3, max: 20, message: '账号长度须在3-20位之间', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '姓名不能为空', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '密码不能为空', trigger: 'blur' },
    { min: 6, message: '密码最少6位', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '手机号不能为空', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的11位手机号', trigger: 'blur' }
  ],
  age: [
    { required: true, message: '年龄不能为空', trigger: 'blur' },
    { pattern: /^(1[89]|[2-9]\d|100)$/, message: '年龄必须在18到100岁之间', trigger: 'blur' }
  ],
  sex: [
    { required: true, message: '必须选择性别', trigger: 'change' }
  ]
}

const handleAdd = () => {
  // 🌟 核心：新增时自动赋予管理员身份 roleId: 1
  form.value = { id: '', no: '', name: '', password: '', phone: '', age: '', sex: null, roleId: 1 }
  dialogVisible.value = true 
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

const handleEdit = (row) => {
  form.value = JSON.parse(JSON.stringify(row)) 
  dialogVisible.value = true
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

const handleClose = (done) => {
  done()
}

const saveUser = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      const url = form.value.id ? '/user/update' : '/user/add'
      
      request.post(url, form.value).then(() => {
        ElMessage.success(form.value.id ? '修改成功！' : '新增管理员成功！')
        dialogVisible.value = false
        loadData()
      }).catch(() => {
        console.log("操作失败，拦截器已提示用户")
      })
    } else {
      ElMessage.warning('请检查表单中标红的错误！')
      return false
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
    `您确定要删除管理员【${row.name}】吗？`,
    '系统高危警告',
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '点错了',
      type: 'warning',
    }
  ).then(() => {
    request.delete('/user/' + row.id).then(() => {
      ElMessage.success('删除成功！')
      loadData() 
    }).catch(() => {
      console.log("删除请求失败")
    })
  }).catch(() => {
    ElMessage.info('已取消删除操作')
  })
}

loadData() 
</script>

<style scoped>
</style>