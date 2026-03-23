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

        <el-button v-if="currentUser.roleId !== 2" type="success" plain @click="openRecord(1)" style="margin-left: 20px;">
          <el-icon style="margin-right: 5px;"><Bottom /></el-icon> 入库
        </el-button>
        <el-button v-if="currentUser.roleId !== 2" type="danger" plain @click="openRecord(2)">
          <el-icon style="margin-right: 5px;"><Top /></el-icon> 出库
        </el-button>

        <el-button v-if="currentUser.roleId !== 2" type="success" @click="handleAdd" style="margin-left: auto;">
          <el-icon style="margin-right: 5px;"><Plus /></el-icon> 新增物品
        </el-button>
      </div>
      
      <el-table 
        :data="tableData" 
        border 
        stripe 
        highlight-current-row
        @current-change="handleRowSelect"
        :header-cell-style="{ background: '#f4f6f8', color: '#333' }"
      >
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
        
        <el-table-column v-if="currentUser.roleId !== 2" label="操作" width="180" fixed="right" align="center">
          <template #default="scope">
            <el-button size="small" type="primary" plain @click.stop="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" plain @click.stop="handleDelete(scope.row)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑物品' : '新增物品'" width="35%">
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

    <el-dialog v-model="recordDialogVisible" :title="recordForm.actionType === 1 ? '物品入库' : '物品出库'" width="35%">
      <el-form ref="recordFormRef" :model="recordForm" :rules="recordRules" label-width="90px">
        <el-form-item label="操作物品">
          <el-input v-model="recordForm.goodsName" disabled /> 
        </el-form-item>
        
        <el-form-item label="办理人员" prop="userName">
          <el-input v-model="recordForm.userName" placeholder="点击选择办理人员" readonly @click="openUserPicker">
            <template #append>
              <el-button :icon="Search" @click="openUserPicker" />
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="操作数量" prop="count">
          <el-input-number v-model="recordForm.count" :min="1" :max="9999" style="width: 100%;" />
        </el-form-item>

        <el-form-item label="备注信息">
          <el-input v-model="recordForm.remark" type="textarea" :rows="2" placeholder="填写单据备注..." />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="recordDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitRecord">确认提交</el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog v-model="userDialogVisible" title="选择办理人员" width="50%" append-to-body>
      <div style="margin-bottom: 15px; display: flex; gap: 10px;">
        <el-input v-model="searchUserName" placeholder="搜索姓名..." style="width: 200px;" clearable @keyup.enter="loadUserData"/>
        <el-button type="primary" @click="loadUserData">搜索</el-button>
      </div>

      <el-table :data="userData" border stripe height="300">
        <el-table-column prop="id" label="ID" width="60" align="center" />
        <el-table-column prop="no" label="工号" width="100" />
        <el-table-column prop="name" label="姓名" />
        <el-table-column prop="roleId" label="角色">
          <template #default="scope">
            <el-tag v-if="scope.row.roleId === 0" type="danger">超级管理员</el-tag>
            <el-tag v-else-if="scope.row.roleId === 1" type="success">管理员</el-tag>
            <el-tag v-else type="info">普通用户</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" align="center" fixed="right">
          <template #default="scope">
            <el-button type="success" size="small" @click="selectUser(scope.row)">选择</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div style="margin-top: 15px; display: flex; justify-content: flex-end;">
        <el-pagination
          v-model:current-page="userPageNum" v-model:page-size="userPageSize"
          layout="prev, pager, next" :total="userTotal" @current-change="loadUserData"
        />
      </div>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Top, Bottom } from '@element-plus/icons-vue'
import request from '../utils/request.js' 

// 🌟 核心修改 3：在顶层声明当前用户信息，供 template 里的 v-if 使用
const currentUser = ref(JSON.parse(localStorage.getItem('user') || '{}'))

// ====== 物品管理核心状态 ======
const tableData = ref([]) 
const pageNum = ref(1)    
const pageSize = ref(10)  
const total = ref(0)      
const searchKeyword = ref('')   
const searchStorage = ref('')
const searchGoodsType = ref('')
const storageData = ref([])
const goodsTypeData = ref([])

// ====== 加载数据与字典 ======
const loadDictionaries = () => {
  request.get('/storage/list').then(res => { storageData.value = res.data || res || [] })
  request.get('/goodstype/list').then(res => { goodsTypeData.value = res.data || res || [] })
}

const loadData = () => {
  const queryParam = {
    pageNum: pageNum.value, 
    pageSize: pageSize.value, 
    keyword: searchKeyword.value, 
    storage: searchStorage.value, 
    goodsType: searchGoodsType.value
  }
  request.post('/goods/list/page', queryParam).then(res => {
    tableData.value = res.records || [] 
    total.value = res.total || 0        
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
  searchKeyword.value = ''; searchStorage.value = ''; searchGoodsType.value = ''; pageNum.value = 1; loadData() 
}

const handleSizeChange = (val) => { pageSize.value = val; pageNum.value = 1; loadData() }
const handleCurrentChange = (val) => { pageNum.value = val; loadData() }

// ====== 新增/编辑功能 ======
const dialogVisible = ref(false) 
const formRef = ref(null)
const form = ref({ id: '', name: '', storage: '', goodsType: '', count: 0, remark: '' })
const rules = {
  name: [{ required: true, message: '不能为空', trigger: 'blur' }],
  storage: [{ required: true, message: '请选择', trigger: 'change' }],
  goodsType: [{ required: true, message: '请选择', trigger: 'change' }]
}

const handleAdd = () => {
  form.value = { id: '', name: '', storage: '', goodsType: '', count: 0, remark: '' }
  dialogVisible.value = true 
}

const handleEdit = (row) => {
  form.value = JSON.parse(JSON.stringify(row)) 
  dialogVisible.value = true
}

const saveGoods = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      const url = form.value.id ? '/goods/update' : '/goods/add'
      request.post(url, form.value).then(() => {
        ElMessage.success('操作成功！')
        dialogVisible.value = false
        loadData() 
      })
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定删除吗？`, '警告', { type: 'warning' }).then(() => {
    request.delete('/goods/' + row.id).then(() => { ElMessage.success('删除成功！'); loadData() })
  })
}

// ==========================================
// 🌟 核心新增：出入库全套逻辑
// ==========================================

const currentRow = ref(null) 

const handleRowSelect = (val) => {
  currentRow.value = val
}

const recordDialogVisible = ref(false)
const recordFormRef = ref(null)
const recordForm = ref({
  actionType: 1, 
  goods: '',     
  goodsName: '', 
  userId: '',    
  userName: '',  
  count: 1,      
  remark: ''     
})

const recordRules = {
  userName: [{ required: true, message: '请选择办理人员', trigger: 'change' }],
  count: [{ required: true, message: '请输入数量', trigger: 'blur' }]
}

const openRecord = (type) => {
  if (!currentRow.value) {
    ElMessage.warning('请先在下方表格中点击选择一个物品！')
    return
  }
  recordForm.value = {
    actionType: type,
    goods: currentRow.value.id,
    goodsName: currentRow.value.name,
    userId: '',
    userName: '',
    count: 1,
    remark: ''
  }
  recordDialogVisible.value = true
  if (recordFormRef.value) recordFormRef.value.clearValidate()
}

// ====== 人员选择器 ======
const userDialogVisible = ref(false)
const searchUserName = ref('')
const userData = ref([])
const userPageNum = ref(1)
const userPageSize = ref(5) 
const userTotal = ref(0)

const openUserPicker = () => {
  userDialogVisible.value = true
  loadUserData() 
}

const loadUserData = () => {
  const queryParam = {
    pageNum: userPageNum.value, 
    pageSize: userPageSize.value, 
    param: { name: searchUserName.value } 
  }
  request.post('/user/list/page', queryParam).then(res => {
    userData.value = res.records || []
    userTotal.value = res.total || 0
  })
}

const selectUser = (userRow) => {
  recordForm.value.userId = userRow.id
  recordForm.value.userName = userRow.name 
  userDialogVisible.value = false 
  recordFormRef.value.validateField('userName') 
}

const submitRecord = () => {
  recordFormRef.value.validate((valid) => {
    if (valid) {
      
      const submitData = {
        goods: recordForm.value.goods,
        userId: recordForm.value.userId,
        // 这里可以直接用刚刚声明好的 currentUser 变量
        adminId: currentUser.value.id, 
        count: recordForm.value.count,
        remark: recordForm.value.remark,
        actionType: recordForm.value.actionType
      }
      
      request.post('/record/addRecord', submitData).then(res => {
        if(!res || res?.code === '200' || res?.code === 200) {
           ElMessage.success(recordForm.value.actionType === 1 ? '入库成功！' : '出库成功！')
           recordDialogVisible.value = false
           loadData() 
        } else {
           ElMessage.error(res?.msg || '操作失败')
        }
      }).catch(err => {
         console.error("前端执行报错：", err)
         ElMessage.error('手慢了！请检查库存数量或联系管理员')
      })
    }
  })
}

onMounted(() => {
  loadDictionaries()
  loadData()
})
</script>

<style scoped>
:deep(.el-table__body tr.current-row > td.el-table__cell) {
  background-color: #e1f3d8 !important;
}
</style>