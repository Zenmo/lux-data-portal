import React, {FunctionComponent} from "react"
import {BsPersonAdd} from "react-icons/bs"

export enum UserFormMode {
    Create = "create",
    Edit = "edit",
}

type UserFormHeaderProps = {
    mode: UserFormMode;
};

export const UserFormHeader: FunctionComponent<UserFormHeaderProps> = ({mode}) => {
    const isEditing = mode === UserFormMode.Edit
    return (
        <div className={"d-flex align-items-center"}>
            <div
                className={"d-flex align-items-center rounded-4 p-2 me-3 bg-light border border-1 border-light-subtle shadow-sm"}>
                <BsPersonAdd size={32} className={"text-primary-emphasis"} />
            </div>
            <div className={"d-flex flex-column"}>
                <span className={"fw-bold text-primary-emphasis"}>
                    {isEditing ? "Gebruiker bewerken" : "Gebruiker toevoegen"}
                </span>
                <span className={"form-text text-muted m-0"}>
                    {isEditing
                        ? "Beheer gebruikersinformatie en rechten"
                        : "Maak een nieuw gebruikersaccount aan en wijs projecttoegang toe."
                    }
                </span>
            </div>
        </div>
    )
}