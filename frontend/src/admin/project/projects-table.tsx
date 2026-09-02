import React, {FunctionComponent, useState} from "react"
import {DataTable} from "primereact/datatable"
import {Column} from "primereact/column"
import {Project} from "zero-zummon"
import {ActionButtonPair} from "../../components/helpers/ActionButtonPair"
import {deleteSurvey} from "../delete-button"
import {useNavigate} from "react-router-dom"

type ProjectsTableProps = {
    projects: Project[]
    loading: boolean
    removeProject?: (projectId: string) => void
    showActions?: boolean
}

export const ProjectsTable: FunctionComponent<ProjectsTableProps> = ({
    projects,
    loading,
    removeProject,
    showActions = true,
}) => {
    const navigate = useNavigate()
    const [pending, setPending] = useState(false)

    return (
        <DataTable
            value={projects}
            loading={loading}
            sortField="name"
            sortOrder={1}
            showGridlines={true}
            stripedRows
            paginator
            rows={10}
            className={"rounded rounded-4"}
        >
            <Column field="name" header="Name" sortable filter filterPlaceholder="Search by name" />
            <Column align={"right"} field="energiekeRegioId" header="Energie Regio Id" sortable filter />
            <Column
                field="buurtCodes"
                header="Buurtcodes"
                body={(project: Project) => {
                    const codes = project.buurtCodes as unknown as string[]
                    return codes.length > 4 ? `${codes.length} buurtcodes` : codes.join(", ")
                }}
            />
            {showActions && (
                <Column
                    header={"Acties"}
                    align={"right"}
                    body={(project: Project) => (
                        <div className={"d-flex flex-row gap-2 justify-content-end"}>
                            <ActionButtonPair
                                positiveAction={() => navigate(`/projects/${project.id}/`)}
                                negativeAction={() => {
                                    deleteSurvey({
                                        id: project.id,
                                        type: "projects",
                                        onDelete: removeProject!,
                                        setPending,
                                    }).then()
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
                    )}
                />
            )}
        </DataTable>
    )
}