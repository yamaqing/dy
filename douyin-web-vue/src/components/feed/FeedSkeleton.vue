<template>
  <!-- Feed 首屏骨架屏：模拟侧边栏与底部文案区，降低白屏感知 -->
  <div class="absolute inset-0 flex animate-pulse items-end justify-between bg-ink p-4 pb-8">
    <div class="flex-1 space-y-3 pr-16">
      <div class="h-4 w-28 rounded bg-white/15" />
      <div class="h-3 w-full rounded bg-white/10" />
      <div class="h-3 w-2/3 rounded bg-white/10" />
      <div class="h-3 w-24 rounded bg-white/10" />
    </div>
    <div class="flex flex-col items-center gap-5">
      <div class="h-12 w-12 rounded-full bg-white/15" />
      <div class="h-9 w-9 rounded-full bg-white/15" />
      <div class="h-9 w-9 rounded-full bg-white/15" />
      <div class="h-9 w-9 rounded-full bg-white/15" />
      <div class="h-9 w-9 rounded-full bg-white/15" />
    </div>
  </div>
</template>
