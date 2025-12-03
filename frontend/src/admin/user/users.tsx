import React, {FunctionComponent, useState} from "react"
import {DataTable} from "primereact/datatable"
import {Column} from "primereact/column"
import {useUsers} from "./use-users"
import {PrimeReactProvider} from "primereact/api"
import {Project, User} from "zero-zummon"

import "primereact/resources/themes/lara-light-cyan/theme.css"
import "primeicons/primeicons.css"
import {deleteSurvey} from "../delete-button"
import {Button} from "primereact/button"
import {useNavigate} from "react-router-dom"
import {Content} from "../../components/Content"
import {ZeroLayout} from "../../components/zero-layout"
import {ActionButtonPair} from "../../components/helpers/ActionButtonPair"

export const Users: FunctionComponent = () => {
    const {loadingUsers, users, changeUser, removeUser} = useUsers()
    const navigate = useNavigate()
    const [pending, setPending] = useState(false)

    return (
        <PrimeReactProvider>
            <Content>
                <ZeroLayout
                    subtitle="Users List"
                    trailingContent={
                        <Button
                            label="Nieuw"
                            icon="pi pi-pencil"
                            onClick={() => navigate(`/users/new-user`)}
                            className="rounded rounded-3"
                        />
                    }
                >
                    <div className={"card border border-0 shadow-lg rounded rounded-4"}>
                        <div className={"card-body p-0"}>

                            <DataTable
                                value={users}
                                loading={loadingUsers}
                                sortField="created"
                                sortOrder={-1}
                                showGridlines={true}
                                paginator
                                rows={10}
                                className={"rounded rounded-4"}
                            >
                                <Column field="note" header="Note" sortable filter filterPlaceholder="Search by note" />
                                <Column
                                    field="isAdmin"
                                    header="Admin"
                                    body={(user: User) => (
                                        <div style={{textAlign: "center"}}>
                                            {user.isAdmin ? (
                                                <span style={{color: "green"}}>✔</span>
                                            ) : (
                                                <span style={{color: "red"}}>✘</span>
                                            )}
                                        </div>
                                    )}
                                />
                                <Column
                                    field="projects"
                                    header="Projects"
                                    body={(user: User) => (
                                        <ul>
                                            {((user.projects) as any).map((project: Project) => (
                                                <li key={project.id}>{project.name}</li>
                                            ))}
                                        </ul>
                                    )}
                                />
                                <Column
                                    header={"Acties"}
                                    align={"right"}
                                    body={(user: User) => (
                                        <div className={"d-flex flex-row gap-2 justify-content-end"}>
                                            <ActionButtonPair
                                                positiveAction={() => {
                                                    navigate(`/users/${user.id}/`)
                                                }}
                                                negativeAction={() => {
                                                    deleteSurvey(
                                                        {
                                                            id: user.id,
                                                            type: "users",
                                                            onDelete: removeUser,
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
