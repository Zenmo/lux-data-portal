import React, {FormEvent, FunctionComponent, useEffect, useState} from "react"
import {useNavigate, useParams} from "react-router-dom"
import {PrimeReactProvider} from "primereact/api"
import {InputText} from "primereact/inputtext"
import {Project} from "zero-zummon"
import {redirectToLogin} from "./use-projects"
import {Content} from "../../components/Content"
import {ActionButtonPair} from "../../components/helpers/ActionButtonPair"
import {BsFolderPlus} from "react-icons/bs"

export const ProjectForm: FunctionComponent = () => {
    const {projectId} = useParams<{ projectId: string }>()
    const [project, setProject] = useState<Project | null>(null)
    const [originalData, setOriginalData] = useState<Project | null>(null)

    const [loading, setLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)
    const navigate = useNavigate()

    const handleCancel = () => {
        setProject(originalData)
        navigate(-1)
    }

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const {name, value} = e.target
        setError(null)
        setProject((prev) => ({...prev, [name]: value} as Project))
    }

    useEffect(() => {
        if (projectId) {
            const fetchProject = async () => {
                setLoading(true)
                try {
                    const response = await fetch(`${import.meta.env.VITE_ZTOR_URL}/projects/${projectId}`, {
                        credentials: "include",
                    })
                    if (response.status === 401) {
                        redirectToLogin()
                        return
                    }
                    if (response.ok) {
                        const projectData = await response.json()
                        setProject(projectData)
                        setOriginalData(projectData)
                    } else {
                        alert(`Error fetching project: ${response.statusText}`)
                    }
                } catch (error) {
                    alert((error as Error).message)
                } finally {
                    setLoading(false)
                }
            }
            fetchProject()
        }
    }, [projectId])

    const handleSubmit = async (event: FormEvent) => {
        event.preventDefault()
        setLoading(true)
        try {
            const method = projectId ? "PUT" : "POST"
            const url = `${import.meta.env.VITE_ZTOR_URL}/projects`
            const response = await fetch(url, {
                method,
                headers: {
                    "Content-Type": "application/json",
                },
                credentials: "include",
                body: JSON.stringify(project),
            })
            if (response.status === 401) {
                redirectToLogin()
                return
            }

            if (response.ok) {
                navigate(`/projects`)
            } else {
                const body = await response.json().catch(() => null)
                const message = body?.error?.message ?? response.statusText
                setError(message)
            }
        } finally {
            setLoading(false)
        }
    }

    return (
        <PrimeReactProvider>
            <Content>
                <div className={"d-flex flex-md-row flex-column gap-4 align-items-start justify-content-center"}>
                    <div className={"col-lg-4 col-md-8 col-12 d-flex flex-column gap-4"}>
                        <div className={"d-flex align-items-center"}>
                            <div
                                className={"d-flex align-items-center rounded-4 p-2 me-3 bg-light border border-1 border-light-subtle shadow-sm"}>
                                <BsFolderPlus size={32} className={"text-primary-emphasis"} />
                            </div>
                            {projectId ? (
                                <span className={"fw-bold text-primary-emphasis"}>Project bewerken: {project?.name}</span>
                            ) : (
                                <span className={"fw-bold text-primary-emphasis"}>Maak een nieuw project aan.</span>
                            )}
                        </div>
                        <form onSubmit={handleSubmit}>
                            <div className={"card bg-light shadow-sm border-light-subtle rounded-4"}>
                                <div className={"card-header align-content-center"}>
                                    <h6 className={"card-title p-2 m-0"}>Projectgegevens</h6>
                                </div>
                                <div className={"card-body p-4 d-flex flex-column gap-3"}>
                                    <div className="fv-row">
                                        <label htmlFor="name"
                                               className={"form-label text-primary-emphasis"}>Naam:</label>
                                        <InputText
                                            id="name"
                                            name="name"
                                            value={project?.name || ""}
                                            onChange={handleInputChange}
                                            className={"form-control bg-transparent" + (error ? " is-invalid" : "")}
                                        />
                                        {error && <div className="invalid-feedback d-block">{error}</div>}
                                    </div>
                                    <div className="fv-row">
                                        <label htmlFor="energiekeRegioId"
                                               className={"form-label text-primary-emphasis"}>
                                            Energieke Regio ID:
                                        </label>
                                        <InputText
                                            id="energiekeRegioId"
                                            name="energiekeRegioId"
                                            value={project?.energiekeRegioId?.toString() || ""}
                                            onChange={handleInputChange}
                                            className={"form-control bg-transparent"}
                                        />
                                    </div>
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
                                        negativeButtonType={"submit"}
                                        negativeText={loading ? "Saving..." : "Save"}
                                        negativeDisabled={loading}
                                        positiveDisabled={loading}
                                        className={"d-flex flex-row gap-3"}
                                    />
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </Content>
        </PrimeReactProvider>
    )
}