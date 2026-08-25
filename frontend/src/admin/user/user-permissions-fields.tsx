import React, {FunctionComponent} from "react"
import {Project, User} from "zero-zummon"
import {BsShield} from "react-icons/bs"
import {ProjectsDropdown} from "../project/projects-dropdown"

type UserPermissionsFieldsProps = {
    user: User | null;
    onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
    selectedProjects: Project[];
    onProjectsChange: (projects: Project[]) => void;
};

export const UserPermissionsFields: FunctionComponent<UserPermissionsFieldsProps> = ({
    user,
    onChange,
    selectedProjects,
    onProjectsChange,
}) => {
    return (
        <div className={"d-flex flex-sm-row flex-column gap-4 align-items-center"}>
            <div className={"col-sm-6 col-12"}>
                <div
                    className={`d-flex flex-column gap-3 p-4 bg-white rounded-4 border border-1 ${user?.isAdmin ? "bg-primary-subtle" : "bg-white"}`}>
                    <div className={"d-flex justify-content-between"}>
                        <div className={"d-flex gap-2 align-items-center"}>
                            <BsShield />
                            <span className={"form-text fw-bold text-primary-emphasis m-0"}>Admin</span>
                        </div>
                        <div className="form-check form-switch">
                            <input
                                className="form-check-input"
                                type="checkbox"
                                role="switch"
                                id="isAdmin"
                                name="isAdmin"
                                checked={user?.isAdmin || false}
                                onChange={onChange}
                            />
                        </div>
                    </div>
                    <p className={"form-text text-muted m-0"}>
                        Volledige toegang tot alle platforminstellingen en gebruikersbeheer.
                    </p>
                </div>
            </div>
            <div className={"col-sm-6 col-12"}>
                <ProjectsDropdown
                    selectedProjects={selectedProjects}
                    onChange={onProjectsChange}
                />
                {selectedProjects.length < 1 && (
                    <p className={"form-text text-muted"}>
                        Wijs de gebruiker toe aan één of meer projecten.
                    </p>
                )}
            </div>
        </div>
    )
}
