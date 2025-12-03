import React, {FunctionComponent} from "react"
import {PrimeReactProvider} from "primereact/api"
import "primereact/resources/themes/lara-light-cyan/theme.css"
import "primeicons/primeicons.css"
import {ZeroLayout} from "../../components/zero-layout"
import {AdminButtonRow} from "./admin-button-row"
import {Content} from "../../components/Content"
import {SurveyTable} from "./survey-table"

export const Surveys: FunctionComponent = () => {
    return (
        <PrimeReactProvider>
            <Content>
                <ZeroLayout
                    subtitle="Beheer uitvraag bedrijven"
                    trailingContent={<AdminButtonRow />}
                >
                    <div className={"card border border-0 shadow-lg rounded rounded-4"}>
                        <div className={"card-body p-0"}>
                            <SurveyTable />
                        </div>
                    </div>
                </ZeroLayout>
            </Content>
        </PrimeReactProvider>
    )
}
