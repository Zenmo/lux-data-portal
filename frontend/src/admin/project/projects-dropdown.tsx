import React, {FunctionComponent, useEffect, useState} from "react";

import {MultiSelect} from "primereact/multiselect";
import {Project, projectsFromJson} from "zero-zummon";

type ProjectDropdownProps = {
    selectedProjects: Project[];
    onChange: (selectedProjects: Project[]) => void;
    disabled?: boolean;
};

export const ProjectsDropdown: FunctionComponent<ProjectDropdownProps> = ({
                                                                              selectedProjects,
                                                                              onChange,
                                                                              disabled,
                                                                          }) => {
    const [projects, setProjects] = useState<Project[]>([]);

    useEffect(() => {
        const fetchProjects = async () => {
            try {
                const response = await fetch(`${import.meta.env.VITE_ZTOR_URL}/all-projects`, {
                    credentials: "include",
                });
                if (!response.ok) {
                    throw new Error(`Failed: ${response.statusText}`)
                }

                setProjects(projectsFromJson(await response.text()))
            } catch (error) {
                console.error("Error fetching projects:", error);
            }
        };

        fetchProjects();
    }, []);

    return (
        <div className={"d-flex flex-row gap-2"}>
            <label htmlFor="projects" className={"form-label me-2"}>Update Projects: </label>
            <MultiSelect
                id="projects"
                options={projects.map((project) => ({
                    label: project.name,
                    value: project,
                }))}
                value={selectedProjects}
                onChange={(e) => onChange(e.value)}
                disabled={disabled}
            />
        </div>
    );
};
