import React, {FunctionComponent, useState} from "react"
import {DataTable} from "primereact/datatable"
import {Column} from "primereact/column"
import {useSurveys} from "./use-surveys"
import {PrimeReactProvider} from "primereact/api"
import {IndexSurvey} from "joshi"
import "primereact/resources/themes/lara-light-cyan/theme.css"
import "primeicons/primeicons.css"
import {deleteSurvey} from "./delete-button"
import {ZeroLayout} from "../components/zero-layout"

import {AdminButtonRow} from "./admin-button-row"
import {SurveyIncludeInSimulationCheckbox} from "./survey-include-in-simulation-checkbox"
import {ActionButtonPair} from "../components/helpers/ActionButtonPair"
import {useNavigate} from "react-router-dom"
import {Content} from "../components/Content"
import {IndexSurveySelectAction} from "./index-survey-select-action"

export const Surveys: FunctionComponent = () => {
    const {
        loading,
        indexSurveys,
        removeIndexSurvey,
        changeSurvey,
    } = useSurveys()

    const navigate = useNavigate()
    const [pending, setPending] = useState(false)

    return (
        <PrimeReactProvider>
            <Content>
                <ZeroLayout
                    subtitle="Beheer uitvraag bedrijven"
                    trailingContent={<AdminButtonRow />}
                >
                    <div className={"card border border-0 shadow-lg rounded rounded-4"}>
                        <div className={"card-body p-0"}>
                            <DataTable
                                value={indexSurveys}
                                loading={loading}
                                showGridlines={true}
                                paginator
                                rows={10}
                                className={"rounded rounded-4"}
                            >
                                <Column field="companyName" header="Bedrijf" sortable
                                        filter filterPlaceholder="Search by company"
                                />
                                <Column field="projectName" header="Project" sortable
                                        filter filterPlaceholder="Search by project"
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
                                                negativeAction={() => {
                                                    deleteSurvey(
                                                        {
                                                            id: survey.id,
                                                            type: "company-surveys",
                                                            onDelete: (id) => removeIndexSurvey(id.toString()),
                                                            setPending: setPending,
                                                        },
                                                    ).then()
                                                }}
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
                        </div>
                    </div>
                </ZeroLayout>
            </Content>
        </PrimeReactProvider>
    )
}

const downloadUrl = (blobName: string) =>
    import.meta.env.VITE_ZTOR_URL + "/download?blobName=" + encodeURIComponent(blobName)

// Doing it in JavaScript because no timezone available in Kotlin.
const formatDatetime = (date: string) => {
    const dateTime = new Date(date)

    return dateTime.getFullYear() + "-" +
        (dateTime.getMonth() + 1).toString().padStart(2, "0") + "-" +
        dateTime.getDate().toString().padStart(2, "0") +
        " " + dateTime.getHours().toString().padStart(2, "0") +
        ":" + dateTime.getMinutes().toString().padStart(2, "0")
}
