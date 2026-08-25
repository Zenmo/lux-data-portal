import React, {FunctionComponent} from "react"
import {InputText} from "primereact/inputtext"
import {User} from "zero-zummon"

type UserDetailsFieldsProps = {
    user: User | null;
    onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
};

export const UserDetailsFields: FunctionComponent<UserDetailsFieldsProps> = ({user, onChange}) => {
    return (
        <>
            <div className="d-flex flex-column">
                <label htmlFor="id" className={"form-label text-primary-emphasis"}>Keycloak ID:</label>
                <InputText
                    id="id"
                    name="id"
                    value={user?.id || ""}
                    onChange={onChange}
                    placeholder={"e.g. 550e8400-e29b-41d4-a716-446655440000"}
                    className={"form-control bg-transparent"}
                />
                <p className={"form-text text-muted"}>
                    De unieke identificatie van de gebruikerin Keycloak.
                </p>
            </div>
            <div className="d-flex flex-column">
                <label htmlFor="note" className={"form-label text-primary-emphasis"}>Note:</label>
                <InputText
                    id="note"
                    name="note"
                    placeholder={"Voeg context toe over de rol of het doel van deze gebruiker."}
                    value={user?.note || ""}
                    onChange={onChange}
                    className={"form-control bg-transparent"}
                />
            </div>
        </>
    )
}