<template>
 <div></div>
</template>

<script lang="ts">
import { Component, Prop, PropSync, Ref } from "vue-property-decorator";
import Vue from "vue";
// @ts-ignore
import { SystemService } from "opensilex-core/index";
// @ts-ignore
import HttpResponse, { OpenSilexResponse } from "opensilex-security/HttpResponse";

@Component
export default class SystemView extends Vue {
  $opensilex: any;
  $store: any;

  service: SystemService;
  

  }
</script>

<style scoped lang="scss">
</style>
