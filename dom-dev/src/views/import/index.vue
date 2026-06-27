<template>
  <div class="page">

    <!-- ━━ カスタムステッパー ━━ -->
    <div class="glass-panel stepper-panel">
      <div class="stepper">
        <div v-for="(s, i) in steps" :key="i" class="step-wrap">
          <div class="step-item" :class="{ active: step >= i, done: step > i }">
            <div class="step-circle">
              <el-icon v-if="step > i" class="step-check"><Check /></el-icon>
              <span v-else>{{ i + 1 }}</span>
            </div>
            <span class="step-label">{{ s }}</span>
          </div>
          <div v-if="i < steps.length - 1" class="step-line" :class="{ done: step > i }"></div>
        </div>
      </div>
    </div>

    <!-- ━━ ステップ①：アップロード ━━ -->
    <div class="glass-panel step-card" :class="{ 'step-card--pending': step !== 0 }">
      <div class="step-card-header">
        <div class="step-num-badge" :class="{ 'badge--dim': step !== 0 }">1</div>
        <span class="step-card-title" :class="{ 'title--dim': step !== 0 }">アップロード</span>
      </div>

      <div v-if="step === 0" class="step-body">
        <!-- データ種別 -->
        <div class="field-group">
          <label class="field-label">取り込みデータ種別</label>
          <el-select v-model="importType" class="dark-select type-select">
            <el-option label="入居履歴" value="residence" />
            <el-option label="空き室情報" value="vacancy" />
            <el-option label="寮費データ" value="rent" />
            <el-option label="備品データ" value="equipment" />
          </el-select>
        </div>

        <!-- ドロップゾーン -->
        <div
          class="drop-zone"
          :class="{ 'drop-zone--hover': isDragOver, 'drop-zone--has-file': uploadFile }"
          @dragover.prevent="isDragOver = true"
          @dragleave="isDragOver = false"
          @drop.prevent="handleDrop"
          @click="triggerFileInput"
        >
          <input ref="fileInputRef" type="file" accept=".xlsx,.xls" style="display:none" @change="handleInputChange" />

          <div v-if="!uploadFile" class="drop-content">
            <div class="upload-cloud">
              <el-icon class="cloud-icon"><UploadFilled /></el-icon>
            </div>
            <p class="drop-main-text">ファイルをここにドラッグ＆ドロップ</p>
            <p class="drop-sub-text">または <span class="drop-link">クリックして選択</span></p>
            <p class="drop-limit">最大ファイルサイズ：50MB</p>
          </div>

          <div v-else class="drop-content file-selected">
            <el-icon class="file-icon"><Document /></el-icon>
            <p class="file-name">{{ uploadFile.name }}</p>
            <p class="file-size">{{ (uploadFile.size / 1024 / 1024).toFixed(2) }} MB</p>
            <button class="btn-clear" @click.stop="uploadFile = null">
              <el-icon><Close /></el-icon>
              変更する
            </button>
          </div>
        </div>

        <!-- フォーマットバッジ -->
        <div class="format-badges">
          <span class="format-label">対応フォーマット</span>
          <span class="badge-format">.xlsx</span>
          <span class="badge-format">.xls</span>
        </div>

        <div class="step-footer">
          <button class="btn-primary" :disabled="!uploadFile || uploading" @click="handleUpload">
            <span class="btn-shimmer"></span>
            <el-icon v-if="uploading" class="is-loading"><Loading /></el-icon>
            <el-icon v-else><ArrowRight /></el-icon>
            <span>{{ uploading ? 'アップロード中...' : '次へ' }}</span>
          </button>
        </div>
      </div>

      <p v-else class="pending-note">✓ {{ uploadFile?.name || 'ファイル選択済み' }}</p>
    </div>

    <!-- ━━ ステップ②③④ — 初期待機：3カラム横並び ━━ -->
    <div v-if="step === 0" class="pending-grid">
      <div v-for="(card, i) in pendingCards" :key="i" class="glass-panel pending-card">
        <div class="step-card-header">
          <div class="step-num-badge badge--dim">{{ i + 2 }}</div>
          <span class="step-card-title title--dim">{{ card.title }}</span>
        </div>
        <p class="pending-note pending-prereq">{{ card.prereq }}</p>
      </div>
    </div>

    <!-- ━━ ステップ②：マッピング ━━ -->
    <div v-if="step >= 1" class="glass-panel step-card" :class="{ 'step-card--pending': step !== 1 }">
      <div class="step-card-header">
        <div class="step-num-badge" :class="{ 'badge--dim': step !== 1 }">2</div>
        <span class="step-card-title" :class="{ 'title--dim': step !== 1 }">マッピング</span>
      </div>

      <div v-if="step === 1" class="step-body">
        <div class="info-banner">
          <el-icon><InfoFilled /></el-icon>
          <span>左側のExcelカラムを右側のシステム項目に対応付けてください</span>
        </div>
        <el-table :data="mappingRows" class="dark-table">
          <el-table-column label="Excelヘッダー" prop="excelCol" min-width="160" />
          <el-table-column label="システム項目" min-width="200">
            <template #default="{ row }">
              <el-select v-model="row.systemField" clearable placeholder="対応項目を選択" class="dark-select" style="width:100%">
                <el-option
                  v-for="f in systemFields"
                  :key="f.value"
                  :label="`${f.label}${f.required ? ' *' : ''}`"
                  :value="f.value"
                />
              </el-select>
            </template>
          </el-table-column>
        </el-table>
        <div class="step-actions">
          <button class="btn-ghost" @click="step = 0">
            <el-icon><ArrowLeft /></el-icon>
            戻る
          </button>
          <button class="btn-primary" :disabled="validating" @click="handleValidate">
            <span class="btn-shimmer"></span>
            <el-icon v-if="validating" class="is-loading"><Loading /></el-icon>
            <el-icon v-else><View /></el-icon>
            <span>{{ validating ? '検証中...' : 'プレビュー' }}</span>
          </button>
        </div>
      </div>

      <p v-else class="pending-note">✓ マッピング設定済み</p>
    </div>

    <!-- ━━ ステップ③：プレビュー ━━ -->
    <div v-if="step >= 2" class="glass-panel step-card" :class="{ 'step-card--pending': step !== 2 }">
      <div class="step-card-header">
        <div class="step-num-badge" :class="{ 'badge--dim': step !== 2 }">3</div>
        <span class="step-card-title" :class="{ 'title--dim': step !== 2 }">プレビュー</span>
      </div>

      <div v-if="step === 2" class="step-body">
        <div class="calc-summary">
          <div class="summary-chip">
            <span class="chip-label">全件数</span>
            <span class="chip-value">{{ previewData.total }} 件</span>
          </div>
          <div class="summary-chip summary-chip--success">
            <span class="chip-label">正常</span>
            <span class="chip-value">{{ previewData.valid }} 件</span>
          </div>
          <div class="summary-chip" :class="previewData.error > 0 ? 'summary-chip--warn' : 'summary-chip--success'">
            <span class="chip-label">エラー</span>
            <span class="chip-value">{{ previewData.error }} 件</span>
          </div>
        </div>

        <el-table :data="previewData.rows" class="dark-table" max-height="380">
          <el-table-column label="行" prop="rowNum" width="70" align="center" />
          <el-table-column v-for="col in previewData.cols" :key="col" :label="col" :prop="col" min-width="120" />
          <el-table-column label="エラー内容" prop="error" min-width="200">
            <template #default="{ row }">
              <span v-if="row.error" class="error-text">{{ row.error }}</span>
              <span v-else class="ok-badge">正常</span>
            </template>
          </el-table-column>
        </el-table>

        <div class="step-actions">
          <button class="btn-ghost" @click="step = 1">
            <el-icon><ArrowLeft /></el-icon>
            戻る
          </button>
          <button class="btn-ghost" @click="resetAll">
            <el-icon><Close /></el-icon>
            キャンセル
          </button>
          <button class="btn-primary" :disabled="executing" @click="handleExecute">
            <span class="btn-shimmer"></span>
            <el-icon v-if="executing" class="is-loading"><Loading /></el-icon>
            <el-icon v-else><Upload /></el-icon>
            <span>{{ executing ? '取り込み中...' : 'エラーを除いて取り込む' }}</span>
          </button>
        </div>
      </div>

      <p v-else class="pending-note">{{ step < 2 ? 'マッピング後に確認できます' : '✓ 確認済み' }}</p>
    </div>

    <!-- ━━ ステップ④：完了 ━━ -->
    <div v-if="step >= 3" class="glass-panel step-card">
      <div class="step-card-header">
        <div class="step-num-badge">4</div>
        <span class="step-card-title">完了</span>
      </div>

      <div class="step-body">
        <div class="success-state">
          <div class="success-icon">
            <el-icon><CircleCheckFilled /></el-icon>
          </div>
          <p class="success-title">データ取り込みが完了しました</p>
          <p class="success-sub">
            取り込み成功：{{ result.success }} 件 ／ スキップ（エラー）：{{ result.skipped }} 件
          </p>
          <div class="success-actions">
            <button class="btn-primary" @click="resetAll">
              <span class="btn-shimmer"></span>
              <el-icon><Refresh /></el-icon>
              <span>続けて取り込む</span>
            </button>
            <button class="btn-ghost" @click="$router.push('/dashboard')">
              <el-icon><HomeFilled /></el-icon>
              <span>ダッシュボードへ</span>
            </button>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import { uploadFile as apiUpload, validateImport, executeImport } from '@/api/importData'

const step = ref(0)
const importType = ref('residence')
const uploadFile = ref(null)
const uploading = ref(false)
const validating = ref(false)
const executing = ref(false)
const uploadedFileId = ref(null)
const mappingRows = ref([])
const previewData = reactive({ total: 0, valid: 0, error: 0, rows: [], cols: [] })
const result = reactive({ success: 0, skipped: 0 })
const isDragOver = ref(false)
const fileInputRef = ref(null)

const steps = ['アップロード', 'マッピング', 'プレビュー', '完了']
const pendingCards = [
  { title: 'マッピング',  prereq: 'ファイルのアップロード後に設定できます' },
  { title: 'プレビュー', prereq: 'マッピング設定後に確認できます' },
  { title: '完了',       prereq: 'プレビュー確認後に実行できます' },
]

const fieldMap = {
  residence: [
    { label: '社員ID',   value: 'employeeId',   required: true  },
    { label: '部屋ID',   value: 'roomId',        required: true  },
    { label: '入居日',   value: 'checkinDate',   required: true  },
    { label: '退寮日',   value: 'checkoutDate',  required: false },
  ],
  vacancy: [
    { label: '部屋ID',   value: 'roomId',        required: true  },
    { label: '空き状態', value: 'status',         required: true  },
  ],
  rent: [
    { label: '社員ID',   value: 'employeeId',    required: true  },
    { label: '対象月',   value: 'yearMonth',      required: true  },
    { label: '金額',     value: 'amount',         required: true  },
  ],
  equipment: [
    { label: '備品名',   value: 'name',           required: true  },
    { label: '寮ID',     value: 'dormitoryId',    required: true  },
    { label: '数量',     value: 'quantity',        required: true  },
  ],
}

const systemFields = computed(() => fieldMap[importType.value] || [])

const triggerFileInput = () => fileInputRef.value?.click()

const handleInputChange = (e) => {
  const file = e.target.files?.[0]
  if (file) uploadFile.value = file
}

const handleDrop = (e) => {
  isDragOver.value = false
  const file = e.dataTransfer.files?.[0]
  if (file) uploadFile.value = file
}

const handleUpload = async () => {
  uploading.value = true
  try {
    const fd = new FormData()
    fd.append('file', uploadFile.value)
    fd.append('type', importType.value)
    const data = await apiUpload(fd)
    uploadedFileId.value = data.fileId
    mappingRows.value = data.headers.map((h) => ({ excelCol: h, systemField: '' }))
    step.value = 1
  } finally { uploading.value = false }
}

const handleValidate = async () => {
  validating.value = true
  try {
    const mapping = mappingRows.value.reduce((acc, r) => {
      if (r.systemField) acc[r.excelCol] = r.systemField
      return acc
    }, {})
    const data = await validateImport({ fileId: uploadedFileId.value, mapping, type: importType.value })
    Object.assign(previewData, data)
    step.value = 2
  } finally { validating.value = false }
}

const handleExecute = async () => {
  executing.value = true
  try {
    const data = await executeImport({
      fileId: uploadedFileId.value,
      type: importType.value,
      skipErrors: true,
    })
    Object.assign(result, data)
    step.value = 3
  } finally { executing.value = false }
}

const resetAll = () => {
  step.value = 0
  uploadFile.value = null
  uploadedFileId.value = null
  mappingRows.value = []
}
</script>

<style scoped>
.page {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-height: 100%;
}

/* ━━ 共通グラスカード ━━ */
.glass-panel {
  background: rgba(255,255,255,0.05);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border: 1px solid rgba(255,255,255,0.09);
  border-radius: 12px;
  padding: 20px;
}

/* ━━ ステッパー ━━ */
.stepper-panel { padding: 16px 24px; }
.stepper { display: flex; align-items: center; }
.step-wrap { display: flex; align-items: center; flex: 1; }
.step-wrap:last-child { flex: 0; }
.step-item { display: flex; flex-direction: column; align-items: center; gap: 8px; flex-shrink: 0; }
.step-circle {
  width: 36px; height: 36px;
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 14px; font-weight: 600;
  background: rgba(255,255,255,0.08);
  border: 1.5px solid rgba(255,255,255,0.18);
  color: rgba(255,255,255,0.35);
  transition: all 0.3s;
}
.step-item.active .step-circle {
  background: linear-gradient(135deg, #4f8ef7, #7c3aed);
  border-color: transparent;
  color: #ffffff;
  box-shadow: 0 0 16px rgba(79,142,247,0.5);
}
.step-item.done .step-circle {
  background: linear-gradient(135deg, #4f8ef7, #7c3aed);
  border-color: transparent;
  color: #ffffff;
  box-shadow: 0 0 10px rgba(79,142,247,0.3);
}
.step-check { font-size: 16px; }
.step-label {
  font-size: 12px;
  color: rgba(255,255,255,0.28);
  white-space: nowrap;
  transition: color 0.3s;
}
.step-item.active .step-label { color: #ffffff; font-weight: 600; }
.step-item.done .step-label  { color: rgba(255,255,255,0.6); }
.step-line {
  flex: 1;
  height: 1.5px;
  background: rgba(255,255,255,0.12);
  margin: 0 8px;
  margin-bottom: 22px;
  transition: background 0.3s;
}
.step-line.done { background: linear-gradient(90deg, #4f8ef7, #7c3aed); }

/* ━━ ステップカード ━━ */
.step-card { transition: border-color 0.3s, opacity 0.3s; }
.step-card--pending {
  border-style: dashed;
  border-color: rgba(255,255,255,0.06);
  opacity: 0.75;
}
.step-card-header { display: flex; align-items: center; gap: 12px; margin-bottom: 20px; }
.step-num-badge {
  width: 28px; height: 28px;
  border-radius: 50%;
  background: linear-gradient(135deg, #4f8ef7, #7c3aed);
  display: flex; align-items: center; justify-content: center;
  color: #ffffff; font-size: 13px; font-weight: 700;
  box-shadow: 0 0 12px rgba(79,142,247,0.4);
  flex-shrink: 0; transition: all 0.3s;
}
.badge--dim { background: rgba(255,255,255,0.1); box-shadow: none; color: rgba(255,255,255,0.3); }
.step-card-title { color: #ffffff; font-size: 15px; font-weight: 600; }
.title--dim { color: rgba(255,255,255,0.3); }
.pending-note { color: rgba(255,255,255,0.25); font-size: 13px; padding: 4px 0; }
.step-body { display: flex; flex-direction: column; gap: 20px; }

/* ━━ フィールドラベル ━━ */
.field-group { display: flex; flex-direction: column; gap: 6px; max-width: 260px; }
.field-label {
  color: rgba(255,255,255,0.38);
  font-size: 10px; font-weight: 600;
  letter-spacing: 0.12em; text-transform: uppercase;
}

/* ━━ ドロップゾーン ━━ */
.drop-zone {
  border: 1.5px dashed rgba(79,142,247,0.35);
  border-radius: 12px;
  padding: 48px 24px;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer;
  background: rgba(79,142,247,0.03);
  min-height: 190px;
  transition: border-color 0.2s, background 0.2s;
}
.drop-zone:hover,
.drop-zone--hover {
  border-color: rgba(79,142,247,0.72);
  background: rgba(79,142,247,0.07);
}
.drop-zone--has-file {
  border-color: rgba(79,142,247,0.55);
  background: rgba(79,142,247,0.06);
}
.drop-content { display: flex; flex-direction: column; align-items: center; gap: 8px; text-align: center; }
.upload-cloud {
  width: 64px; height: 64px;
  border-radius: 50%;
  background: rgba(79,142,247,0.13);
  display: flex; align-items: center; justify-content: center;
  margin-bottom: 4px;
}
.cloud-icon { font-size: 30px; color: #4f8ef7; }
.drop-main-text { color: rgba(255,255,255,0.65); font-size: 14px; font-weight: 500; }
.drop-sub-text { color: rgba(255,255,255,0.35); font-size: 13px; }
.drop-link { color: #4f8ef7; font-weight: 600; text-decoration: underline; }
.drop-limit { color: rgba(255,255,255,0.22); font-size: 12px; margin-top: 4px; }

/* ファイル選択後 */
.file-selected { gap: 6px; }
.file-icon { font-size: 38px; color: #4f8ef7; }
.file-name { color: #ffffff; font-size: 14px; font-weight: 500; }
.file-size { color: rgba(255,255,255,0.4); font-size: 12px; }
.btn-clear {
  display: inline-flex; align-items: center; gap: 4px;
  margin-top: 8px; padding: 5px 12px;
  background: transparent;
  border: 1px solid rgba(255,255,255,0.2);
  border-radius: 6px;
  color: rgba(255,255,255,0.5); font-size: 12px; cursor: pointer;
  transition: background 0.15s, color 0.15s;
}
.btn-clear:hover { background: rgba(255,255,255,0.08); color: #ffffff; }

/* ━━ フォーマットバッジ ━━ */
.format-badges { display: flex; align-items: center; gap: 8px; }
.format-label { color: rgba(255,255,255,0.3); font-size: 12px; }
.badge-format {
  padding: 3px 10px;
  background: rgba(34,197,94,0.10);
  border: 1px solid rgba(34,197,94,0.32);
  border-radius: 20px;
  color: #22c55e; font-size: 12px; font-weight: 700; font-family: monospace;
}

/* ━━ 次へボタンフッター ━━ */
.step-footer { display: flex; justify-content: flex-end; }

/* ━━ 初期待機3カラム ━━ */
.pending-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}
.pending-card { padding: 16px 20px; }
.pending-prereq { color: rgba(255,255,255,0.22); font-size: 12px; line-height: 1.5; margin-top: 4px; }

/* ━━ インフォバナー ━━ */
.info-banner {
  display: flex; align-items: center; gap: 8px;
  padding: 10px 14px;
  background: rgba(79,142,247,0.1);
  border: 1px solid rgba(79,142,247,0.22);
  border-radius: 8px;
  color: rgba(255,255,255,0.65); font-size: 13px;
}

/* ━━ プレビューサマリー ━━ */
.calc-summary { display: flex; gap: 12px; flex-wrap: wrap; }
.summary-chip {
  display: flex; align-items: center; gap: 8px;
  padding: 8px 14px;
  background: rgba(255,255,255,0.06);
  border: 1px solid rgba(255,255,255,0.09);
  border-radius: 8px;
}
.summary-chip--success { background: rgba(34,197,94,0.10); border-color: rgba(34,197,94,0.25); }
.summary-chip--warn    { background: rgba(245,108,108,0.10); border-color: rgba(245,108,108,0.25); }
.chip-label { color: rgba(255,255,255,0.4); font-size: 12px; }
.chip-value { color: #ffffff; font-size: 14px; font-weight: 600; }
.error-text { color: #f56c6c; font-size: 13px; }
.ok-badge {
  display: inline-block; padding: 2px 8px;
  background: rgba(34,197,94,0.12);
  border: 1px solid rgba(34,197,94,0.3);
  border-radius: 10px;
  color: #22c55e; font-size: 11px;
}

/* ━━ アクション行 ━━ */
.step-actions { display: flex; gap: 12px; margin-top: 4px; }

/* ━━ ボタン ━━ */
.btn-primary {
  position: relative;
  display: inline-flex; align-items: center; gap: 8px;
  padding: 10px 22px;
  background: linear-gradient(135deg, #4f8ef7, #7c3aed);
  border: none; border-radius: 8px;
  color: #ffffff; font-size: 14px; font-weight: 500;
  cursor: pointer; overflow: hidden;
  box-shadow: 0 4px 16px rgba(79,142,247,0.38);
  transition: box-shadow 0.2s, opacity 0.2s;
  align-self: flex-start;
}
.btn-primary:hover { box-shadow: 0 6px 24px rgba(79,142,247,0.52); }
.btn-primary:disabled { opacity: 0.65; cursor: not-allowed; }
.btn-shimmer {
  position: absolute; top: 0; left: -75%;
  width: 50%; height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.22), transparent);
  transform: skewX(-20deg);
  animation: shimmer 2.8s infinite;
}
@keyframes shimmer { 0% { left: -75%; } 60% { left: 125%; } 100% { left: 125%; } }

.btn-ghost {
  display: inline-flex; align-items: center; gap: 6px;
  padding: 10px 18px;
  background: transparent;
  border: 1px solid rgba(255,255,255,0.18);
  border-radius: 8px;
  color: rgba(255,255,255,0.65); font-size: 14px;
  cursor: pointer; transition: background 0.15s, color 0.15s;
}
.btn-ghost:hover { background: rgba(255,255,255,0.08); color: #ffffff; }

/* ━━ ダークセレクト ━━ */
.type-select { width: 240px; }
:deep(.dark-select .el-select__wrapper) {
  background: rgba(255,255,255,0.06) !important;
  box-shadow: 0 0 0 1px rgba(255,255,255,0.12) inset !important;
  border-radius: 8px;
}
:deep(.dark-select .el-select__wrapper:hover) {
  box-shadow: 0 0 0 1px rgba(255,255,255,0.24) inset !important;
}
:deep(.dark-select .el-select__wrapper.is-focused) {
  box-shadow: 0 0 0 1px #4f8ef7 inset, 0 0 12px rgba(79,142,247,0.22) !important;
}
:deep(.dark-select .el-select__selected-item) { color: #ffffff; }
:deep(.dark-select .el-select__placeholder) { color: rgba(255,255,255,0.28); }
:deep(.dark-select .el-select__caret) { color: rgba(255,255,255,0.38); }

/* ━━ ダークテーブル ━━ */
.dark-table {
  --el-table-bg-color: transparent;
  --el-table-tr-bg-color: transparent;
  --el-table-header-bg-color: transparent;
  --el-table-border-color: rgba(255,255,255,0.07);
  --el-table-row-hover-bg-color: rgba(255,255,255,0.05);
  --el-fill-color-lighter: transparent;
  background: transparent !important;
}
:deep(.el-table__inner-wrapper::before) { display: none; }
:deep(.el-table .el-table__header th.el-table__cell) {
  background: transparent;
  color: rgba(255,255,255,0.32); font-size: 10px; font-weight: 600;
  text-transform: uppercase; letter-spacing: 0.10em;
  border-bottom: 1px solid rgba(255,255,255,0.08);
}
:deep(.el-table .el-table__body td.el-table__cell) {
  background: transparent;
  color: rgba(255,255,255,0.78);
  border-bottom: 1px solid rgba(255,255,255,0.05); font-size: 13px;
}
:deep(.el-table__body tr:hover > td.el-table__cell) {
  background: rgba(255,255,255,0.05) !important;
}

/* ━━ 完了 ━━ */
.success-state {
  display: flex; flex-direction: column; align-items: center;
  padding: 20px 0 8px; gap: 12px; text-align: center;
}
.success-icon { font-size: 52px; color: #22c55e; filter: drop-shadow(0 0 16px rgba(34,197,94,0.5)); }
.success-title { color: #ffffff; font-size: 16px; font-weight: 600; }
.success-sub { color: rgba(255,255,255,0.38); font-size: 13px; max-width: 420px; line-height: 1.6; }
.success-actions { display: flex; gap: 12px; margin-top: 8px; }
</style>
