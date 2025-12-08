import {FunctionComponent, useMemo} from "react"
import {useSurveyPagination} from "./use-survey-pagination"
import {DataTable, DataTableFilterMeta, DataTableFilterMetaData, DataTableStateEvent} from "primereact/datatable"
import {IndexSurvey, SurveyOrder, SurveyOrderField, OrderDirection, FetchIndexSurveysRequest} from "joshi"
import {Column} from "primereact/column"
import {Nullable} from "primereact/ts-helpers"
import {SortOrder} from "primereact/api"
import {SurveyIncludeInSimulationCheckbox} from "./survey-include-in-simulation-checkbox"
import {IndexSurveySelectAction} from "./index-survey-select-action"
import {ActionButtonPair} from "../../components/helpers/ActionButtonPair"
import {useNavigate} from "react-router-dom"

export const SurveyTable: FunctionComponent = () => {
    const {
        pending, indexSurveys, totalCount, request, setRequest, changeSurvey, deleteSurvey
    } = useSurveyPagination()

    const navigate = useNavigate()

    const processEvent = (event: DataTableStateEvent) => {
        let surveyOrder: Nullable<SurveyOrder> = null
        if (event.sortField && event.sortOrder == -1 || event.sortOrder == 1) {
            surveyOrder = new SurveyOrder(
                SurveyOrderField.fromFieldName(event.sortField),
                OrderDirection.fromInt(event.sortOrder)
            )
        }

        let newRequest = request
            .withLimit(event.rows)
            .withOffset((event.page ?? 0) * event.rows)
            .withOrder(surveyOrder)

        // set new filters if they are in the event, else keep the existing ones
        if (event.filters) {
            newRequest = newRequest
                .withProjectSearch((event.filters.projectName as DataTableFilterMetaData).value)
                .withCompanySearch((event.filters.companyName as DataTableFilterMetaData).value)
        }

        setRequest(newRequest)
    }

    return (
        <DataTable
            lazy
            filterDisplay="row"
            value={indexSurveys}
            loading={pending}
            showGridlines={true}
            paginator
            rowsPerPageOptions={[5, 10, 20, 50, 100]}
            first={request.offset ?? 0}
            rows={request.limit ?? undefined}
            totalRecords={totalCount}
            onPage={processEvent}
            sortField={request.order?.field.fieldName}
            sortOrder={request.order?.direction.int as SortOrder}
            onSort={processEvent}
            onFilter={processEvent}
            filters={requestToFilters(request)}
            className={"rounded rounded-4"}
        >
            <Column field="companyName" header="Bedrijf" sortable
                    filter filterPlaceholder="Search by company" showFilterMenu={false}
            />
            <Column field="projectName" header="Project" sortable
                    filter filterPlaceholder="Search by project" showFilterMenu={false}
            />
            <Column field="creationDate"
                    body={(survey: IndexSurvey) => formatDatetime(survey.creationDate.toString())}
                    header="Opgestuurd op" sortable />
            <Column field="includeInSimulation" header="Opnemen in simulatie" sortable
                    align={"center"}
                    body={(survey: IndexSurvey) =>
                        <SurveyIncludeInSimulationCheckbox
                            includeInSimulation={survey.includeInSimulation}
                            surveyId={survey.id}
                            setIncludeInSimulation={(includeInSimulation) => {
                                changeSurvey(survey.withIncludeInSimulation(includeInSimulation))
                            }}
                        />
                    }
            />
            <Column
                header={"Acties"}
                align={"right"}
                body={(survey: IndexSurvey) => (
                    <div className={"d-flex flex-row gap-2 justify-content-end"}>
                        <IndexSurveySelectAction indexSurvey={survey} />

                        <ActionButtonPair
                            positiveAction={() => {
                                navigate(`/bedrijven-uitvraag/${survey.id}/`)
                            }}
                            negativeAction={() => deleteSurvey(survey.id)}
                            positiveIcon="pencil"
                            negativeIcon="trash"
                            positiveClassName="bg-secondary-subtle text-dark border border-0"
                            negativeClassName="bg-danger"
                            showNegative={true}
                            className={"d-flex flex-row align-items-center gap-2"}
                            positiveSeverity={"secondary"}
                            negativeSeverity={"danger"}
                            negativeLoading={pending}
                        />
                    </div>
                )} />
        </DataTable>
    )
}

// Doing it in JavaScript because no timezone available in Kotlin.
const formatDatetime = (date: string) => {
    const dateTime = new Date(date)

    return dateTime.getFullYear() + "-" +
        (dateTime.getMonth() + 1).toString().padStart(2, "0") + "-" +
        dateTime.getDate().toString().padStart(2, "0") +
        " " + dateTime.getHours().toString().padStart(2, "0") +
        ":" + dateTime.getMinutes().toString().padStart(2, "0")
}

function requestToFilters(request: FetchIndexSurveysRequest): DataTableFilterMeta {
    return {
        companyName: {
            value: request.companySearch ?? "",
            matchMode: "contains",
        },
        projectName: {
            value: request.projectSearch ?? "",
            matchMode: "contains",
        },
    }
}
