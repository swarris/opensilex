<template>
  <div class="container-fluid">
    <div class="row">
      <div class="col-md-6">
        <!-- Facilities -->
        <opensilex-FacilitiesView
            :withActions="true"
            @onUpdate="refresh"
            @onCreate="refresh"
            @onDelete="refresh"
            :isSelectable="true"
            ref="facilitiesView"
            @facilitySelected="updateSelectedFacility"
            :displayButtonOnTop="true"
        ></opensilex-FacilitiesView>
      </div>
      <div class="col-md-6">
        <!-- Facility detail -->
        <opensilex-FacilityDetail
            :selected="selectedFacility"
        >
        </opensilex-FacilityDetail>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import OpenSilexVuePlugin from "../../models/OpenSilexVuePlugin";
import {OrganizationsService} from "opensilex-core/api/organizations.service";
import {Component, Ref} from "vue-property-decorator";
import Vue from "vue";
import HttpResponse, {OpenSilexResponse} from "../../lib/HttpResponse";
import FacilitiesView from "./FacilitiesView.vue";
import { InfrastructureFacilityGetDTO } from 'opensilex-core/index';

@Component
export default class FacilityListView extends Vue {
  $opensilex: OpenSilexVuePlugin;

  service: OrganizationsService;

  selectedFacility: InfrastructureFacilityGetDTO = null;

  @Ref("facilitiesView")
  facilitiesView: FacilitiesView;

  created() {
    this.service = this.$opensilex.getService(
        "opensilex-core.OrganizationsService"
    );
  }

  get user() {
    return this.$store.state.user;
  }

  get credentials() {
    return this.$store.state.credentials;
  }

  updateSelectedFacility(facility: InfrastructureFacilityGetDTO) {
    if (!facility || !facility.uri) {
      this.selectedFacility = undefined;
      return;
    }

    this.service
        .getInfrastructureFacility(facility.uri)
        .then((http: HttpResponse<OpenSilexResponse<InfrastructureFacilityGetDTO>>) => {
          this.selectedFacility = http.response.result;
        });
  }

  refresh() {
    this.facilitiesView.refresh();
  }
}
</script>

<style scoped>

</style>

<i18n>
en:
  FacilityListView:
    description:
      Manage and configure facilities
fr:
  FacilityListView:
    description:
      Gérer et configurer les installations environnementales
</i18n>