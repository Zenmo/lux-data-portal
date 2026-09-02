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
        <DataTable
            value={projects}
            paginator
            showGridlines={true}
            stripedRows
            header={
                <h3>Current Projects</h3>
            }
            rows={10}
            sortField="name"
            sortOrder={1}>
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
    )
}