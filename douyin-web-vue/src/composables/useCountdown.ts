import { onBeforeUnmount, ref } from 'vue'

/** 短信验证码倒计时 */
export function useCountdown(seconds: number) {
  const remaining = ref(0)
  let timer: number | undefined

  function start() {
    if (remaining.value > 0) return
    remaining.value = seconds
    timer = window.setInterval(() => {
      remaining.value -= 1
      if (remaining.value <= 0) window.clearInterval(timer)
    }, 1000)
  }

  onBeforeUnmount(() => window.clearInterval(timer))

  return { remaining, start }
}
