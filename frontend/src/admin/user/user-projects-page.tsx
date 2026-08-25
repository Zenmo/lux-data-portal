import React, {FunctionComponent, useEffect, useState} from "react"
import {useNavigate, useParams} from "react-router-dom"
import {PrimeReactProvider} from "primereact/api"
import {Project, User} from "zero-zummon"
import {Content} from "../../components/Content"
import {ZeroLayout} from "../../components/zero-layout"
import {redirectToLogin} from "./use-users"
import {ProjectsTable} from "../project/projects-table"

export const UserProjectsPage: FunctionComponent = () => {
    const {userId} = useParams<{ userId: string }>()
    const navigate = useNavigate()
    const [user, setUser] = useState<User | null>(null)
    const [loading, setLoading] = useState(true)
    const [userProjects, setUserProjects] = useState<Project[]>([])

    useEffect(() => {
        const fetchUserData = async () => {
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
                    setUserProjects(userData.projects)
                } else {
                    alert(`Error fetching user: ${response.statusText}`)
                }
            } catch (error) {
                alert((error as Error).message)
            } finally {
                setLoading(false)
            }
        }
        fetchUserData().then()
    }, [userId])

    return (
        <PrimeReactProvider>
            <Content>
                <nav aria-label="breadcrumb" className="mb-4">
                    <ol className="breadcrumb">
                        <li className="breadcrumb-item">
                            <a
                                href="#"
                                onClick={e => {
                                    e.preventDefault()
                                    navigate("/users")
                                }}
                                className="text-muted"
                            >
                                Users
                            </a>
                        </li>
                        <li className="breadcrumb-item active" aria-current="page">
                            {user?.note || userId}
                        </li>
                    </ol>
                </nav>
                <ZeroLayout subtitle={`Projecten voor ${user?.note || "..."}`}>
                    <ProjectsTable
                        projects={userProjects}
                        loading={loading}
                        showActions={false}
                    />
                </ZeroLayout>
            </Content>
        </PrimeReactProvider>
    )
}