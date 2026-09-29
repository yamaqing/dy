<script setup lang="ts">
import { nextTick, onBeforeUnmount, ref, watch } from 'vue'
import Icon from '@/components/common/Icon.vue'
import { useCountdown } from '@/composables/useCountdown'
import { useAppStore } from '@/stores/app'
import { useUserStore } from '@/stores/user'

/** 短信验证码登录弹窗（对接 live-api /user/sendSMS、/user/mobileLogin） */
const app = useAppStore()
const user = useUserStore()
const { remaining, start } = useCountdown(60)

const phone = ref('')
const code = ref('')
const phoneErr = ref('')
const codeErr = ref('')
const agreed = ref(false)
const sending = ref(false)
const submitting = ref(false)
/** 校验失败抖动动画开关 */
const shaking = ref(false)
const phoneInputRef = ref<HTMLInputElement>()

// 弹窗每次打开时重置错误与验证码，手机号保留便于重试；桌面端自动聚焦手机号输入框
watch(
  () => app.loginVisible,
  async (v) => {
    if (v) {
      phoneErr.value = ''
      codeErr.value = ''
      code.value = ''
      window.addEventListener('keydown', onEscClose)
      // autoFocus 仅限桌面端（避免移动端弹键盘挤压布局）
      if (window.matchMedia('(pointer: fine)').matches) {
        await nextTick()
        phoneInputRef.value?.focus()
      }
    } else {
      window.removeEventListener('keydown', onEscClose)
    }
  }
)

function onEscClose(e: KeyboardEvent) {
  if (e.key === 'Escape') app.closeLogin()
}

onBeforeUnmount(() => window.removeEventListener('keydown', onEscClose))

function shake() {
  shaking.value = false
  requestAnimationFrame(() => {
    shaking.value = true
    setTimeout(() => (shaking.value = false), 450)
  })
}

async function sendCode() {
  phoneErr.value = /^1[3-9]\d{9}$/.test(phone.value) ? '' : '请输入正确的手机号'
  if (phoneErr.value) return shake()
  if (sending.value || remaining.value > 0) return
  sending.value = true
  try {
    await user.sendCode(phone.value)
    start()
    app.toast('验证码已发送（mock 环境任意 4 位数字可登录）', 'success')
  } catch (e) {
    app.toast(e instanceof Error ? e.message : '发送失败，请重试', 'error')
  } finally {
    sending.value = false
  }
}

async function submit() {
  phoneErr.value = /^1[3-9]\d{9}$/.test(phone.value) ? '' : '请输入正确的手机号'
  codeErr.value = /^\d{4}$/.test(code.value) ? '' : '请输入 4 位数字验证码'
  if (phoneErr.value || codeErr.value) return shake()
  if (!agreed.value) {
    shake()
    app.toast('请先阅读并同意用户协议和隐私政策', 'error')
    return
  }
  if (submitting.value) return
  submitting.value = true
  try {
    await user.login(phone.value, Number(code.value))
    app.closeLogin()
    app.toast('登录成功', 'success')
  } catch (e) {
    app.toast(e instanceof Error ? e.message : '登录失败，请重试', 'error')
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <Teleport to="body">
    <Transition name="modal">
      <div
        v-if="app.loginVisible"
        class="fixed inset-0 z-[90] flex items-center justify-center bg-black/60 backdrop-blur-sm"
        @click.self="app.closeLogin()"
      >
        <div
          role="dialog"
          aria-modal="true"
          aria-labelledby="login-title"
          class="w-[340px] rounded-2xl bg-surface-2 p-6 shadow-2xl ring-1 ring-white/10"
          :class="{ 'animate-shake': shaking }"
        >
          <div class="mb-6 flex items-center justify-between">
            <h2 id="login-title" class="text-lg font-semibold text-txt-1">登录后更精彩</h2>
            <button
              class="rounded-full p-1 text-txt-2 transition-colors hover:bg-white/10 hover:text-txt-1"
              aria-label="关闭登录弹窗"
              @click="app.closeLogin()"
            >
              <Icon name="close" :size="20" />
            </button>
          </div>

          <!-- 手机号 -->
          <div class="mb-1 flex items-center gap-2 rounded-lg bg-white/10 px-3 transition-shadow focus-within:ring-1 focus-within:ring-primary">
            <span class="shrink-0 text-sm text-txt-2" aria-hidden="true">+86</span>
            <input
              ref="phoneInputRef"
              v-model.trim="phone"
              type="tel"
              name="mobile"
              inputmode="numeric"
              autocomplete="tel"
              spellcheck="false"
              maxlength="11"
              placeholder="请输入手机号"
              aria-label="手机号"
              :aria-invalid="!!phoneErr"
              class="h-11 min-w-0 flex-1 bg-transparent text-sm text-txt-1 outline-none placeholder:text-txt-3"
            />
          </div>
          <p class="mb-3 h-4 text-xs text-red-400" aria-live="polite">{{ phoneErr }}</p>

          <!-- 验证码 -->
          <div class="mb-1 flex items-center gap-2 rounded-lg bg-white/10 px-3 transition-shadow focus-within:ring-1 focus-within:ring-primary">
            <input
              v-model.trim="code"
              type="text"
              name="code"
              inputmode="numeric"
              autocomplete="one-time-code"
              spellcheck="false"
              maxlength="4"
              placeholder="请输入 4 位验证码"
              aria-label="短信验证码"
              :aria-invalid="!!codeErr"
              class="h-11 min-w-0 flex-1 bg-transparent text-sm text-txt-1 outline-none placeholder:text-txt-3"
              @keyup.enter="submit"
            />
            <button
              class="shrink-0 text-sm transition-colors"
              :class="remaining > 0 || sending ? 'cursor-not-allowed text-txt-3' : 'text-primary hover:text-primary/80'"
              :disabled="remaining > 0 || sending"
              @click="sendCode"
            >
              {{ remaining > 0 ? `${remaining}s 后重发` : sending ? '发送中…' : '获取验证码' }}
            </button>
          </div>
          <p class="mb-4 h-4 text-xs text-red-400" aria-live="polite">{{ codeErr }}</p>

          <button
            class="flex h-11 w-full items-center justify-center rounded-lg bg-primary text-sm font-medium text-white transition-opacity hover:opacity-90 disabled:opacity-50"
            :disabled="submitting"
            @click="submit"
          >
            {{ submitting ? '登录中…' : '登录' }}
          </button>

          <!-- 协议勾选：label 包裹保证整行可点（无点击死角） -->
          <label class="mt-4 flex cursor-pointer select-none items-start justify-center gap-1.5 text-xs leading-5 text-txt-3">
            <input
              v-model="agreed"
              type="checkbox"
              name="agreement"
              class="mt-0.5 h-3.5 w-3.5 shrink-0 cursor-pointer accent-primary"
            />
            <span>
              已阅读并同意
              <a class="text-txt-2 underline-offset-2 hover:underline" @click.stop.prevent>用户协议</a>
              和
              <a class="text-txt-2 underline-offset-2 hover:underline" @click.stop.prevent>隐私政策</a>
            </span>
          </label>
          <p class="mt-2 text-center text-xs text-txt-3">未注册手机号验证通过后将自动注册（mock 环境任意 4 位数字可登录）</p>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.2s ease;
}
.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}
</style>
