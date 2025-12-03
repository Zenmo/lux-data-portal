import React, {FunctionComponent, useState} from "react"
import {DataTable} from "primereact/datatable"
import {Column} from "primereact/column"
import {useProjects} from "./use-projects"
import {PrimeReactProvider} from "primereact/api"
import {Project} from "zero-zummon"

import "primereact/resources/themes/lara-light-cyan/theme.css"
import "primeicons/primeicons.css"
import {deleteSurvey} from "../delete-button"
import {Button} from "primereact/button"
import {useNavigate} from "react-router-dom"
import {Content} from "../../components/Content"
import {ZeroLayout} from "../../components/zero-layout"
import {ActionButtonPair} from "../../components/helpers/ActionButtonPair"

export const Projects: FunctionComponent = () => {
    const {loadingProjects, projects, changeProject, removeProject} = useProjects()
    const navigate = useNavigate()
    const [pending, setPending] = useState(false)

    return (
        <PrimeReactProvider>
            <Content>
                <ZeroLayout
                    subtitle="Projects List"
                    trailingContent={
                        <Button
                            label="Nieuw"
                            icon="pi pi-pencil"
                            onClick={(event) => navigate(`/projects/new-project`)}
                            className="rounded rounded-3"
                        />
                    }
                >
                    <div className={"card border border-0 shadow-lg rounded rounded-4"}>
                        <div className={"card-body p-0"}>
                            <DataTable
                                value={projects}
                                loading={loadingProjects}
                                sortField="created"
                                sortOrder={-1}
                                showGridlines={true}
                                paginator
                                rows={10}
                                className={"rounded rounded-4"}
                            >
                                <Column field="name" header="Name" sortable filter filterPlaceholder="Search by name" />
                                <Column align={"right"} field="energiekeRegioId" header="Energie Regio Id" sortable
                                        filter />
                                <Column
                                    header={"Acties"}
                                    align={"right"}
                                    body={(project: Project) => (
                                        <div className={"d-flex flex-row gap-2 justify-content-end"}>
                                            <ActionButtonPair
                                                positiveAction={() => {
                                                    navigate(`/projects/${project.id}/`)
                                                }}
                                                negativeAction={() => {
                                                    deleteSurvey(
                                                        {
                                                            id: project.id,
                                                            type: "projects",
                                                            onDelete: removeProject,
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
                                                size={"small"}
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
