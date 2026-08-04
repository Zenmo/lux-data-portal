import {ReactNode} from "react"
import {BsBarChartLine, BsBriefcase, BsFileEarmarkText, BsHouseDoor, BsPeople} from "react-icons/bs"

export type NavItem = {
    to: string
    label: string
    icon: ReactNode
    adminOnly?: boolean
}

export const navItems: NavItem[] = [
    {to: "/", label: "Home", icon: <BsHouseDoor />},
    {to: "/surveys", label: "Surveys", icon: <BsFileEarmarkText />},
    {to: "/projects", label: "Projects", icon: <BsBriefcase />},
    {to: "/users", label: "Users", icon: <BsPeople />, adminOnly: true},
    {to: "/simulation", label: "Simulation", icon: <BsBarChartLine />},
]
