import React, {FunctionComponent} from "react"
import {Project} from "zero-zummon"
import {DataTable} from "primereact/datatable"
import {Column} from "primereact/column"
import {ActionButtonPair} from "../../components/helpers/ActionButtonPair"
import {useNavigate} from "react-router-dom"

type UserProjectsListProps = {
    projects?: Project[];
};

export const UserProjectsList: FunctionComponent<UserProjectsListProps> = ({
    projects,
}) => {
    const navigate = useNavigate()
    return (
        <div className={"card border border-0 shadow-lg rounded rounded-4"}>
            <div className={"card-header bg-white"}>
                <span className={"card-title fs-3 fw-bold"}>Current Projects</span>
            </div>
            <div className={"card-body p-0"}>
                <DataTable value={projects}
                           paginator
                           rows={10}>
                    <Column field="name" header="Project Name" />
                    <Column
                        body={(project: Project) => (
                            <div className={"d-flex flex-row gap-2 justify-content-end"}>
                                <ActionButtonPair
                                    positiveAction={() => {
                                        navigate(`/projects/${project.id}/`)
                                    }}
                                    positiveIcon="pencil"
                                    positiveClassName="bg-secondary-subtle text-dark border border-0"
                                    showNegative={false}
                                    positiveSeverity={"secondary"}
                                    negativeSeverity={"danger"}
                                    size={"small"}
                                />
                            </div>
                        )}
                        header="Actions"
                        align={"right"}
                    />
                </DataTable>
            </div>
        </div>
    )
}
