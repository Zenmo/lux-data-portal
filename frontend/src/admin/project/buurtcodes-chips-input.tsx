import React, {FunctionComponent} from "react"
import {Chips, ChipsChangeEvent} from "primereact/chips"
import {Project} from "zero-zummon"

type BuurtcodesFieldProps = {
    project: Project | null;
    onChange: (project: React.SetStateAction<Project | null>) => void;
};

export const BuurtcodesChipsInput: FunctionComponent<BuurtcodesFieldProps> = ({project, onChange}) => {
    const handleChange = (e: ChipsChangeEvent) => {
        onChange(prev => ({
            ...prev,
            buurtCodes: e.value ?? [],
        } as unknown as Project))
    }

    return (
        <div className="d-flex flex-column">
            <label className={"form-label text-primary-emphasis"}>Buurtcodes:</label>
            <Chips
                id={"buurtCodes"}
                name={"buurtCodes"}
                value={(project?.buurtCodes as unknown as string[]) ?? []}
                onChange={handleChange}
                placeholder="Voer een buurtcode in"
            />
            <small className="form-text text-muted">
                Typ een buurtcode en druk op Enter om toe te voegen. Klik op het kruisje of
                druk op Backspace om een code te verwijderen.
            </small>
        </div>
    )
}