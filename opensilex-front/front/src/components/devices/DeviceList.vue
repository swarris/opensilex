<template>
  <div>
    <opensilex-PageContent
      class="pagecontent"
    >
        <!-- Toggle Sidebar--> 
        <div class="searchMenuContainer"
            v-on:click="SearchFiltersToggle = !SearchFiltersToggle"
            :title="searchFiltersPannel()">
            <div class="searchMenuIcon">
                <i class="icon ik ik-search"></i>
            </div>
        </div>
      <!-- FILTERS -->
      <Transition>
        <div v-show="SearchFiltersToggle">

    <opensilex-SearchFilterField
      @search="refresh()"
      @clear="reset()"
      withButton="false"
      :showAdvancedSearch="true"
      class="searchFilterField"
    >
      <template v-slot:filters>
      <!-- Name --> 
        <div>
          <label>{{ $t("DeviceList.filter.namePattern") }}</label>
          <opensilex-StringFilter
            :filter.sync="filter.name"
            placeholder="DeviceList.filter.namePattern-placeholder"
            class="searchFilter"
          ></opensilex-StringFilter><br>
        </div>

      <!-- Type --> 
        <div>
          <opensilex-TypeForm
            :type.sync="filter.rdf_type"
            :baseType="$opensilex.Oeso.DEVICE_TYPE_URI"
            placeholder="DeviceList.filter.rdfTypes-placeholder"
            class="searchFilter"
          ></opensilex-TypeForm>
        </div>

        <!-- Variables -->
         <div>
            <opensilex-VariableSelectorWithFilter
              placeholder="VariableSelector.placeholder"
              :variables.sync="filter.variable"
              maximumSelectedRows="1"
              class="searchFilter"
            ></opensilex-VariableSelectorWithFilter>
         </div>

        <!-- Start Up --> 
        <div>
          <label>{{ $t("DeviceList.filter.start_up") }}</label>
          <opensilex-StringFilter
            :filter.sync="filter.start_up"
            placeholder="DeviceList.filter.start_up-placeholder"
            type="number"
            class="searchFilter"
          ></opensilex-StringFilter><br>
        </div>

        <!-- Brand --> 
        <div>
          <label>{{ $t("DeviceList.filter.brand") }}</label>
          <opensilex-StringFilter
            :filter.sync="filter.brand"
            placeholder="DeviceList.filter.brand-placeholder"
            class="searchFilter"
          ></opensilex-StringFilter><br>
        </div>

        <!-- Constructor model --> 
        <div>
          <label>{{ $t("DeviceList.filter.model") }}</label>
          <opensilex-StringFilter
            :filter.sync="filter.model"
            placeholder="DeviceList.filter.model-placeholder"
            class="searchFilter"
          ></opensilex-StringFilter><br>
        </div>
      </template>
      <template v-slot:advancedSearch>
        <!-- Key --> 
        <div>
          <label>{{ $t("DeviceList.filter.metadataKey") }}</label>
          <opensilex-StringFilter
            :filter.sync="filter.metadataKey"
            placeholder="DeviceList.filter.metadataKey-placeholder"
            class="searchFilter"
          ></opensilex-StringFilter>
        </div>
        <!-- Value --> 
        <div>
          <label>{{ $t("DeviceList.filter.metadataValue") }}</label>
          <opensilex-StringFilter
            :filter.sync="filter.metadataValue"
            placeholder="DeviceList.filter.metadataValue-placeholder"
            class="searchFilter"
          ></opensilex-StringFilter>
          <br>
        </div>
      </template>
    </opensilex-SearchFilterField>
        </div>
      </Transition>

        <opensilex-TableAsyncView
            ref="tableRef"
            :searchMethod="searchDevices"
            :fields="fields"
            defaultSortBy="name"
            :isSelectable="true"
            labelNumberOfSelectedRow="DeviceList.selected"
            iconNumberOfSelectedRow="ik#ik-thermometer"
        >
            <template v-slot:selectableTableButtons="{ numberOfSelectedRows }">
                <b-dropdown
                    dropright
                    class="mb-2 mr-2"
                    :small="true"
                    :disabled="numberOfSelectedRows == 0"
                    text="actions"
                >
                    <b-dropdown-item-button
                        v-if="user.hasCredential(credentials.CREDENTIAL_DOCUMENT_MODIFICATION_ID)"
                        @click="createDocument()">{{$t('component.common.addDocument')}}
                    </b-dropdown-item-button>
                    <b-dropdown-item-button @click="exportDevices()">{{$t('DeviceList.export')}}
                    </b-dropdown-item-button>
                    <b-dropdown-item-button
                        v-if="user.hasCredential(credentials.CREDENTIAL_DEVICE_MODIFICATION_ID)"
                        @click="linkVariable()">{{$t('DeviceList.linkVariable')}}
                    </b-dropdown-item-button>
                    <b-dropdown-divider></b-dropdown-divider>

                    <!-- <b-dropdown-item-button disabled>{{$t('DeviceList.addAnnotation')}}
                    </b-dropdown-item-button>-->

                    <b-dropdown-item-button
                        v-if="user.hasCredential(credentials.CREDENTIAL_EVENT_MODIFICATION_ID)"
                        @click="createEvents()">{{$t('Event.add-multiple')}}
                    </b-dropdown-item-button>
                    <b-dropdown-item-button
                        v-if="user.hasCredential(credentials.CREDENTIAL_EVENT_MODIFICATION_ID)"
                        @click="createMoves()">{{$t('Move.add')}}
                    </b-dropdown-item-button>
                </b-dropdown>
            </template>

            <template v-slot:cell(name)="{ data }">
                <opensilex-UriLink
                    :uri="data.item.uri"
                    :value="data.item.name"
                    :to="{ path: '/device/details/' + encodeURIComponent(data.item.uri) }"
                ></opensilex-UriLink>
            </template>

            <template v-slot:cell(actions)="{ data }">
                <b-button-group size="sm">
                    <opensilex-EditButton
                        v-if="
              user.hasCredential(credentials.CREDENTIAL_DEVICE_MODIFICATION_ID)
            "
                        @click="editDevice(data.item.uri)"
                        label="DeviceList.update"
                        :small="true"
                    ></opensilex-EditButton>
                    <opensilex-DeleteButton
                        v-if="user.hasCredential(credentials.CREDENTIAL_DEVICE_DELETE_ID)"
                        label="DeviceList.delete"
                        :small="true"
                        @click="deleteDevice(data.item.uri)"
                    ></opensilex-DeleteButton>
                </b-button-group>
            </template>
        </opensilex-TableAsyncView>

        <opensilex-ModalForm
            v-if="user.hasCredential(credentials.CREDENTIAL_DOCUMENT_MODIFICATION_ID)"
            ref="documentForm"
            component="opensilex-DocumentForm"
            createTitle="component.common.addDocument"
            modalSize="lg"
            :initForm="initForm"
            icon="ik#ik-file-text"
        ></opensilex-ModalForm>

        <opensilex-DeviceModalForm
            ref="deviceForm"
            @onUpdate="refresh()"
        ></opensilex-DeviceModalForm>

        <opensilex-VariableModalList
            v-if="showVariableForm"
            label="label"
            ref="variableSelection"
            :isModalSearch="true"
            :required="true"
            :multiple="true"
            @onValidate="editDeviceVar"
        ></opensilex-VariableModalList>

        <opensilex-EventCsvForm
            v-if="showEventForm && user.hasCredential(credentials.CREDENTIAL_EVENT_MODIFICATION_ID)"
            ref="eventCsvForm"
            :targets="selectedUris">
        </opensilex-EventCsvForm>

        <opensilex-EventCsvForm
            v-if="showMoveForm && user.hasCredential(credentials.CREDENTIAL_EVENT_MODIFICATION_ID)"
            ref="moveCsvForm"
            :targets="selectedUris"
            :isMove="true"
        ></opensilex-EventCsvForm>
        </opensilex-PageContent>
    </div>
</template>

<script lang="ts">
import {Component, Ref} from "vue-property-decorator";
import Vue from "vue";
import {DevicesService, DeviceGetDetailsDTO} from "opensilex-core/index";
import HttpResponse, {OpenSilexResponse} from "../../lib/HttpResponse";
import EventCsvForm from "../events/form/csv/EventCsvForm.vue";
import DeviceModalForm from "./form/DeviceModalForm.vue";

@Component
export default class DeviceList extends Vue {
    $opensilex: any;
    service: DevicesService;
    $store: any;
    $route: any;

    @Ref("tableRef") readonly tableRef!: any;
    @Ref("documentForm") readonly documentForm!: any;
    @Ref("deviceForm") readonly deviceForm!: DeviceModalForm;

    @Ref("variableSelection") readonly variableSelection!: any;
    @Ref("eventCsvForm") readonly eventCsvForm!: EventCsvForm;
    @Ref("moveCsvForm") readonly moveCsvForm!: EventCsvForm;

    selectedUris: Array<string> = [];

    get user() {
        return this.$store.state.user;
    }

    get lang() {
        return this.$store.getters.language;
    }

    get credentials() {
        return this.$store.state.credentials;
    }

    showVariableForm: boolean = false;
    showEventForm: boolean = false;
    showMoveForm: boolean = false;
    filter = {
        name: undefined,
        rdf_type: undefined,
        variable: undefined,
        start_up: undefined,
        existence_date: undefined,
        brand: undefined,
        model: undefined,
        metadataKey: undefined,
        metadataValue: undefined,
    };

    data(){
        return {
            SearchFiltersToggle : false,
        }
    }

    exportFilter = {
        name: undefined,
        rdf_type: undefined,
        variable: undefined,
        start_up: undefined,
        existence_date: undefined,
        brand: undefined,
        model: undefined,
        serial_number: undefined,
        metadata: undefined,
    };

    resetFilters() {
        this.filter = {
            name: undefined,
            rdf_type: undefined,
            variable: undefined,
            start_up: undefined,
            existence_date: undefined,
            brand: undefined,
            model: undefined,
            metadataKey: undefined,
            metadataValue: undefined,
        };

    }

    reset() {
        this.resetFilters();
        this.refresh();
    }

    created() {
        this.service = this.$opensilex.getService("opensilex.DevicesService");
        this.$opensilex.updateFiltersFromURL(this.$route.query, this.filter);
    }

    editDevice(uri: string) {
        this.deviceForm.showEditForm(uri);
    }

    deleteDevice(uri: string) {
        this.service
            .deleteDevice(uri)
            .then(() => {
                this.refresh();
                this.$emit("onDelete", uri);
            })
            .catch((error) => {
                if (error.response.result.title && error.response.result.title === "LINKED_DEVICE_ERROR") {
                    let message = this.$i18n.t("DeviceList.associated-device-error") + " " + error.response.result.message;
                    this.$opensilex.showErrorToast(message);
                } else {
                    this.$opensilex.errorHandler(error);
                }
            });
    }

    fields = [
        {
            key: "name",
            label: "DeviceList.name",
            sortable: true,
        },
        {
            key: "rdf_type_name",
            label: "DeviceList.rdfTypes",
            sortable: true,
        },
        {
            key: "start_up",
            label: "DeviceList.start_up",
            sortable: true,
        },
        {
            key: "actions",
            label: "component.common.actions",
        },
    ];

    refresh() {
        this.tableRef.selectAll = false;
        this.tableRef.onSelectAll();
        this.$opensilex.updateURLParameters(this.filter);
        this.tableRef.refresh();
    }

    searchDevices(options) {
        //this.updateExportFilters();
        return this.service.searchDevices(
            this.filter.rdf_type, // rdf_type filter
            true, // include_subtypes boolean,
            this.filter.name, // name filter
            this.filter.variable, // variable filter
            this.filter.start_up, // year filter
            this.filter.existence_date, // existence_date filter
            this.filter.brand, // brand filter
            this.filter.model, // model filter
            undefined, // serial_number filter
            this.addMetadataFilter(), //metadata filter
            options.orderBy,
            options.currentPage,
            options.pageSize
        );
    }


    exportDevices() {
        let path = "/core/devices/export_by_uris";
        let today = new Date();
        let filename =
            "export_devices_" +
            today.getFullYear() +
            String(today.getMonth() + 1).padStart(2, "0") +
            String(today.getDate()).padStart(2, "0");

        var exportList = [];
        for (let select of this.tableRef.getSelected()) {
            exportList.push(select.uri);
        }
        this.$opensilex.downloadFilefromPostService(
            path,
            filename,
            "csv",
            {uris: exportList},
            this.lang
        );
    }

    linkVariable() {
        let typeDevice;
        let measure = [];
        let deniedType = [
            "vocabulary:RadiometricTarget",
            "vocabulary:Station",
            "vocabulary:ControlLaw",
        ];
        for (let select of this.tableRef.getSelected()) {
            typeDevice = select.rdf_type;
            measure.push(deniedType.includes(typeDevice));
        }

        if (measure.includes(true)) {
            alert(this.$t("DeviceList.alertBadDeviceType"));
        } else {
            this.showVariableForm = true;
            this.$nextTick(() => {
                this.variableSelection.show();
            });
        }
    }

    editDeviceVar(variableSelected) {
        for (let select of this.tableRef.getSelected()) {
            this.service
                .getDevice(select.uri)
                .then((http: HttpResponse<OpenSilexResponse<DeviceGetDetailsDTO>>) => {
                    let varList = [];
                    for (let select of variableSelected) {
                        varList.push({
                            property: "vocabulary:measures",
                            value: select.uri,
                        });
                    }
                    let device = http.response.result;
                    let form = JSON.parse(JSON.stringify(device));
                    form.relations = form.relations.concat(varList);
                    this.updateVariable(form);
                })
                .catch(this.$opensilex.errorHandler);
        }
    }

    updateVariable(form) {
        this.$opensilex
            .getService("opensilex.DevicesService")
            .updateDevice(form)
            .then((http: HttpResponse<OpenSilexResponse<any>>) => {
                let uri = http.response.result;
                console.debug("device updated", uri);
                this.$emit("onUpdate", form);
            })
            .catch(this.$opensilex.errorHandler);
    }

    createDocument() {
        this.documentForm.showCreateForm();
    }

    createEvents() {
        this.showEventForm = true;
        this.$nextTick(() => {
            this.updateSelectedUris();
            this.eventCsvForm.show();
        });
    }

    createMoves() {
        this.showMoveForm = true;
        this.$nextTick(() => {
            this.updateSelectedUris();
            this.moveCsvForm.show();
        });
    }

    updateSelectedUris() {
        this.selectedUris = [];
        for (let select of this.tableRef.getSelected()) {
            this.selectedUris.push(select.uri);
        }
    }

    initForm() {
        let targetURI = [];
        for (let select of this.tableRef.getSelected()) {
            targetURI.push(select.uri);
        }

        return {
            description: {
                uri: undefined,
                identifier: undefined,
                rdf_type: undefined,
                title: undefined,
                date: undefined,
                description: undefined,
                targets: targetURI,
                authors: undefined,
                language: undefined,
                deprecated: undefined,
                keywords: undefined,
            },
            file: undefined,
        };
    }

    addMetadataFilter() {
        let metadata = undefined;
        if (
            this.filter.metadataKey != undefined &&
            this.filter.metadataKey != "" &&
            this.filter.metadataValue != undefined &&
            this.filter.metadataValue != ""
        ) {
            metadata =
                '{"' +
                this.filter.metadataKey +
                '":"' +
                this.filter.metadataValue +
                '"}';
            return metadata;
        }
    }

    updateExportFilters() {
        this.exportFilter.name = this.filter.name;
        this.exportFilter.rdf_type = this.filter.rdf_type;
        this.exportFilter.start_up = this.filter.start_up;
        this.exportFilter.existence_date = this.filter.existence_date;
        this.exportFilter.brand = this.filter.brand;
        this.exportFilter.model = this.filter.model;
        this.exportFilter.serial_number = undefined;
        this.exportFilter.metadata = this.addMetadataFilter();
    }
    searchFiltersPannel() {
        return  this.$t("searchfilter.label")
    }
}
</script>

<style scoped lang="scss">
</style>

<i18n>

en:
    DeviceList:
        uri: URI
        name: Name
        rdfTypes: Device Type
        variable: Variable
        start_up: Start up
        update: Update Device
        delete: Delete Device
        selected: Selected devices
        facility: Facility
        linkVariable: Link variables
        export: Export Device list
        alertSelectSize: The selection has too many lines, 1000 lines maximum
        addEvent: Add event
        addAnnotation: Add annotation
        addMove: Move
        showMap: Show in a map
        alertBadDeviceType: The selected type doesn't match with add variable
        associated-device-error: Device is associated with

        filter:
            namePattern: Name
            namePattern-placeholder: Enter name
            rdfTypes: Type
            rdfTypes-placeholder: Select a device type
            variable: Variable
            variable-placeholder: Select a variable
            start_up: Start up
            start_up-placeholder: Enter year
            brand: Brand
            brand-placeholder: Enter brand
            model: Constructor model
            model-placeholder: Enter constructor model
            metadataValue: Value
            metadataKey: Key
            metadataValue-placeholder: Enter value
            metadataKey-placeholder: Enter key

fr:
    DeviceList:
        uri: URI
        name: Nom
        rdfTypes: Type du dispositif
        variable: Variable
        start_up: Date d'obtention
        update: Editer le dispositif
        delete: Supprimer le dispositif
        selected: Dispositifs selectionnés
        facility: Installation technique
        linkVariable: Lier des variables
        export: Exporter la liste
        alertSelectSize: La selection contient trop de ligne, 1000 lignes maximum
        addEvent: Ajouter un évènement
        addAnnotation: Ajouter une annotation
        addMove: Déplacement
        showMap: Afficher sur une carte
        alertBadDeviceType: La selection comporte un type incompatible avec l'ajout de variable
        associated-device-error: Le dispositif est associé à

        filter:
            namePattern: Nom
            namePattern-placeholder: Entrer un nom
            rdfTypes: Type
            rdfTypes-placeholder: Sélectionner un type de dispositif
            variable: Variable
            variable-placeholder: Sélectionner une variable
            start_up: Date d'obtention
            start_up-placeholder: Entrer une année
            brand: Marque
            brand-placeholder: Entrer une marque
            model: Modèle constructeur
            model-placeholder: Entrer le nom du modèle constructeur
            metadataValue: Valeur
            metadataKey: Attribut
            metadataValue-placeholder: Entrer une valeur
            metadataKey-placeholder: Entrer un attribut

</i18n>

