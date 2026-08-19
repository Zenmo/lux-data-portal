import React, {FormEvent, FunctionComponent, useEffect, useRef, useState} from "react"
import {useNavigate, useParams} from "react-router-dom"
import {PrimeReactProvider} from "primereact/api"
import {Project, projectsFromJson, User} from "zero-zummon"
import {redirectToLogin} from "./use-users"
import {UserProjectsList} from "./user-projects-list"
import {Toast} from "primereact/toast"
import {Content} from "../../components/Content"
import {ActionButtonPair} from "../../components/helpers/ActionButtonPair"
import {UserFormHeader, UserFormMode} from "./user-form-header"
import {UserDetailsFields} from "./user-details-fields"
import {UserPermissionsFields} from "./user-permissions-fields"

export const UserForm: FunctionComponent = () => {
    const {userId} = useParams<{ userId: string }>()
    const [user, setUser] = useState<User | null>(null)
    const [originalData, setOriginalData] = useState<User | null>(null)
    const [selectedProjects, setSelectedProjects] = useState<Project[]>([])
    const [userProjects, setUserProjects] = useState<Project[]>([])
    const msgs = useRef<Toast>(null)
    const navigate = useNavigate()

    const [loading, setLoading] = useState(false)

    const handleCancel = () => {
        setUser(originalData)
        setSelectedProjects(userProjects)
        navigate(-1)
    }


    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const {name, value, type, checked} = e.target
        setUser((prev) => ({
            ...prev,
            [name]: type === "checkbox" ? checked : value,
        } as User))
    }

    const transformProjects = (projects: any[]): Project[] => {
        const jsonString = JSON.stringify(projects)
        return projectsFromJson(jsonString)
    }

    useEffect(() => {
        if (userId) {
            const fetchUser = async () => {
                setLoading(true)
                try {
                    const response = await fetch(`${import.meta.env.VITE_ZTOR_URL}/users/${userId}/projects`, {
                        credentials: "include",
                    })
                    if (response.status === 401) {
                        redirectToLogin()
                        return
                    }
                    if (response.ok) {
                        const userData = await response.json()
                        setUser(userData)
                        setOriginalData(userData)
                        setUserProjects(userData.projects)
                        const formattedProjects = transformProjects(userData.projects)
                        setSelectedProjects(formattedProjects)
                    } else {
                        alert(`Error fetching user: ${response.statusText}`)
                    }
                } catch (error) {
                    alert((error as Error).message)
                } finally {
                    setLoading(false)
                }
            }
            fetchUser()
        }
    }, [userId])

    const handleSubmit = async (event: FormEvent) => {
        event.preventDefault()
        setLoading(true)
        try {
            const sendUser = JSON.stringify({
                ...user,
                projects: selectedProjects.map((project) => ({
                    id: project.id.toString(),
                    name: project.name,
                })),
            })
            const method = userId ? "PUT" : "POST"
            const url = `${import.meta.env.VITE_ZTOR_URL}/users`
            const response = await fetch(url, {
                method,
                headers: {
                    "Content-Type": "application/json",
                },
                credentials: "include",
                body: sendUser,
            })

            if (response.status === 401) {
                return
            }
            if (response.ok) {
                msgs.current?.show([
                    {
                        severity: "success",
                        summary: "Success",
                        detail: "User saved successfully.",
                        closable: true,
                    },
                ])
                setUserProjects(selectedProjects)
            } else {
                msgs.current?.show([
                    {
                        severity: "error",
                        summary: "Error",
                        detail: `Error: ${response.statusText}`,
                        closable: true,
                    },
                ])
            }
        } finally {
            setLoading(false)
        }
    }

    return (
        <PrimeReactProvider>
            <Content>
                <Toast ref={msgs} />
                <div
                    className={"d-flex flex-column align-items-center gap-4 align-items-start justify-content-center"}>
                    <div className={"col-lg-5 col-md-8 col-12 d-flex flex-column gap-4"}>
                        <UserFormHeader mode={userId ? UserFormMode.Edit : UserFormMode.Create} />
                        <form onSubmit={handleSubmit}>
                            <div className={"card bg-light shadow-sm border-light-subtle rounded-4"}>
                                <div className={"card-header align-content-center"}>
                                    <h6 className={"card-title p-2 m-0"}>Gebruikersgegevens</h6>
                                </div>
                                <div className={"card-body p-4 d-flex flex-column gap-3"}>
                                    <UserDetailsFields user={user} onChange={handleInputChange} />
                                    <hr />
                                    <span className={"form-text text-primary-emphasis"}>Rechten</span>
                                    <UserPermissionsFields
                                        user={user}
                                        onChange={handleInputChange}
                                        selectedProjects={selectedProjects}
                                        onProjectsChange={setSelectedProjects}
                                    />
                                </div>
                                <div
                                    className={"card-footer bg-white border-0 rounded-bottom-4 d-flex justify-content-end"}>
                                    <ActionButtonPair
                                        positiveText={"Cancel"}
                                        positiveIcon={undefined}
                                        positiveAction={handleCancel}
                                        positiveClassName="bg-secondary-subtle text-dark border border-0"
                                        positiveSeverity={"secondary"}
                                        negativeSeverity={null}
                                        showNegative={true}
                                        negativeText={loading ? "Saving..." : "Save"}
                                        negativeDisabled={loading}
                                        positiveDisabled={loading}
                                        negativeButtonType={"submit"}
                                        className={"d-flex flex-row gap-3"}
                                    />
                                </div>
                            </div>
                        </form>
                    </div>
                    {userId &&
                        <div className={"col-lg-5 col-md-8 col-12"}>
                            <UserProjectsList projects={userProjects} />
                        </div>
                    }
                </div>
            </Content>
        </PrimeReactProvider>
    )
}