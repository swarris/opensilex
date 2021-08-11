<template>
  <div class="container-fluid">
    <opensilex-PageHeader
      icon="ik#ik-layers"
      title="SystemView.title"
      description="SystemView.title-description"
    ></opensilex-PageHeader>
    <opensilex-PageContent>
      <template v-slot>
        <opensilex-Card icon="ik#ik-info" label="SystemView.details">
          <template v-slot:body>
            <h4>{{ $t("SystemView.info") }}</h4>
            <b-row>
              <b-col cols="4">
                <opensilex-StringView
                  label="SystemView.title"
                  :value="version_info.title"
                ></opensilex-StringView>
                <opensilex-StringView
                  label="SystemView.version"
                  :value="version_info.version"
                ></opensilex-StringView>

                <opensilex-UriView
                  title="SystemView.api-docs"
                  :uri="version_info.api_docs.url"
                  :value="version_info.api_docs.description"
                  target="_blank"
                ></opensilex-UriView>

                <opensilex-UriView
                  title="SystemView.git-commit"
                  :uri="version_info.git_commit.commit_id"
                  :value="version_info.git_commit.commit_message"
                  target="_blank"
                >
                </opensilex-UriView>
              </b-col>
              <b-col>
                <opensilex-TextView
                  label="SystemView.description"
                  :value="version_info.description"
                ></opensilex-TextView>

                <opensilex-UriView
                  title="SystemView.contact"
                  :uri="version_info.contact.email"
                  :value="version_info.contact.email"
                  :href="'mailto:' + version_info.contact.email" 
                ></opensilex-UriView>

                <opensilex-UriView
                  title="SystemView.project"
                  :uri="version_info.contact.homepage"
                  value="OpenSILEX homepage"
                  target="_blank"
                ></opensilex-UriView>

                <opensilex-LabelUriView
                  label="SystemView.license"
                  :uri="version_info.license.url"
                  :value="version_info.license.name"
                  target="_blank"
                  :allowCopy="false"
                ></opensilex-LabelUriView>
              </b-col>
            </b-row>

            <hr />
            <h4>{{ $t("SystemView.loaded-modules") }}</h4>
            <opensilex-TableView
              v-if="
                version_info.modules_version != undefined &&
                version_info.modules_version.length > 0
              "
              :items="version_info.modules_version"
              :fields="modulesFields"
              :showCount="false"
              :withPagination="false"
              :pageSize="30"
            >
            </opensilex-TableView>
          </template>
        </opensilex-Card>
      </template>
    </opensilex-PageContent>
  </div>
</template>

<script lang="ts">
import { Component } from "vue-property-decorator";
import Vue from "vue";
// @ts-ignore
import { SystemService, version_infoDTO } from "opensilex-core/index";
// @ts-ignore
import HttpResponse, {
  OpenSilexResponse,
} from "opensilex-security/HttpResponse";

@Component
export default class SystemView extends Vue {
  $opensilex: any;
  $store: any;

  service: SystemService;

  version_info: version_infoDTO = {};

  modulesFields: any[] = [
    {
      key: "name",
      label: "SystemView.name",
      sortable: false,
    },
    {
      key: "version",
      label: "SystemView.version",
      sortable: false,
    },
  ];

  created() {
    this.service = this.$opensilex.getService("opensilex.SystemService");
    this.service
      .getVersionInfo()
      .then((http: HttpResponse<OpenSilexResponse<version_infoDTO>>) => {
        this.version_info = http.response.result;
      })
      .catch((error) => {
        this.$opensilex.errorHandler(error);
      });
  }
}
</script>

<style scoped lang="scss">
</style>
<i18n>
en:
  SystemView:
    title: System
    details: System details
    info: Informations
    version: Version
    project: Project homepage 
    contact: Contact e-mail
    api-docs: API documentation link
    git-commit : Last commit Id
    description : Description
    title-description : Informations about information system
    license: Software license
    loaded-modules: Loaded modules
    name : Name
    
fr:
  SystemView:
    title: Système
    details: Détails du système
    info: Informations
    version: Version
    project: Page du projet
    contact: E-mail du contact
    api-docs: URL Documentation API
    git-commit : Dernier id commit
    description : Description
    title-description : Informations à propos du système d'information
    license: Licence logicielle
    loaded-modules : Modules chargés
    name : Nom
</i18n>
