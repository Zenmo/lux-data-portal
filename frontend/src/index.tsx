import React from 'react'
import ReactDOM from 'react-dom/client'
import './index.css'
import reportWebVitals from './reportWebVitals'
import { RouterProvider } from "react-router-dom";
import { router } from "./router";
import './_zenmo-zero/assets/style.scss'
import './_zenmo-zero/assets/style.react.scss'
import {IndexSurveyClient, FetchIndexSurveysRequest} from "joshi"

const root = ReactDOM.createRoot(
    document.getElementById('root') as HTMLElement,
)
root.render(
    <React.StrictMode>
        <RouterProvider router={router} />
    </React.StrictMode>,
)

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals()

export async function testRpcClient(): Promise<void> {
    const client = new IndexSurveyClient()
    const surveyList = await client.fetchIndexSurveys(new FetchIndexSurveysRequest())
    console.log(surveyList)
}
