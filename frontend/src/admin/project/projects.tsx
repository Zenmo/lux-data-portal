import React, {FunctionComponent} from "react"
import {useProjects} from "./use-projects"
import {PrimeReactProvider} from "primereact/api"
import {Button} from "primereact/button"
import {useNavigate} from "react-router-dom"
import {Content} from "../../components/Content"
import {ZeroLayout} from "../../components/zero-layout"
import {ProjectsTable} from "./projects-table"

export const Projects: FunctionComponent = () => {
    const {loadingProjects, projects, removeProject} = useProjects()
    const navigate = useNavigate()

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
                    <ProjectsTable
                        projects={projects}
                        loading={loadingProjects}
                        removeProject={removeProject}
                        showActions={true}
                    />
                </ZeroLayout>
            </Content>
        </PrimeReactProvider>
    )
}