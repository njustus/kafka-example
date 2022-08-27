<script lang="ts">
import { defineComponent } from 'vue'
import { RouterLink, RouterView } from 'vue-router'
import {SocketConnector} from './socket-connector'


interface SimpleEditRow {
  username: string,
  row: string
}

export default defineComponent({
  created() {
    const connector = new SocketConnector<any>('ws://localhost:4200', (msg) => {
      console.log("received msg:", msg)      
      this.$data.messages.push(msg.value)
      const payload = msg.value?.value?.LineEdit
      if(payload) {
        this.$data.beautified.push({
          username: payload.user.username,
          row: payload.content
        })
      }
    })
    console.log("connector created")
  },

  data(): {messages: any[], beautified: SimpleEditRow[]} {
    return {
      messages: [],
      beautified: []
    }
  },
});

</script>

<template>
  <header>
  </header>

  <section>
    <p v-for="(it, idx) in beautified" v-bind:key="idx">
      <strong>[{{idx}} {{it.username}}]</strong> {{it.row}}
    </p>
  </section>

<!--  <RouterView /> -->
</template>

<style scoped>
strong {
  font-weight: bold;
}
</style>
