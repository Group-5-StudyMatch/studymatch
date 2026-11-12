
import { useEffect, useState } from 'react'
//Grab the routing tools we need from React Router
// NavLink is for clickable links , Outlet is a placeholder for the actual pages.
import { Link as RouterLink, NavLink, Outlet, useLocation } from 'react-router-dom';

import {
  AppBar,
  Avatar,
  Box,
  Breadcrumbs,
  Button,
  Divider,
  Drawer,
  IconButton,
  Link,
  List,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Menu,
  MenuItem,
  Toolbar,
  Typography,
} from '@mui/material'
import MenuIcon from '@mui/icons-material/Menu'
import LogoutIcon from '@mui/icons-material/Logout'
import { buildCrumbs, navItemsFor } from '../navigation'
import { useCurrentUser } from '../hooks/useCurrentUser'

const DRAWER_WIDTH = 260

// set up the main layout shell. The UI stays put while the pages inside it swap out
export default function Layout() {
    
    const { user, logout } =    useCurrentUser()
    const { pathname } = useLocation()
    const [mobileOpen, setMobileOpen] = useState(false)
    const [menuAnchor, setMenuAnchor] = useState(null)

    const crumbs = buildCrumbs(pathname)
    const title = pathname === '/' ? 'Home' : crumbs.at(-1)?.label ?? 'Page not found'
    const items = navItemsFor(user.role)

    useEffect(() => {
        document.title = `${title} | StudyMatch`
        }, [title])

    const sidebar = (
        <Box>
        <Toolbar>
                {/* Logo: when frontend/src/assets/logo.svg exists, replace this text with
            <Box component="img" src={logo} alt="StudyMatch" sx={{ height: 32 }} /> */}
            <Typography
                variant="h6"
                component={RouterLink}
                to="/"
                sx={{ color: 'primary.main', fontWeight: 700, textDecoration: 'none' }}
            >
            StudyMatch
            </Typography>
        </Toolbar>
      <Divider />
      <List component="nav" aria-label="Main navigation" sx={{ px: 1, py: 1 }}>
        {items.map((item) => (
          <ListItemButton
            key={item.to}
            component={NavLink}
            to={item.to}
            onClick={() => setMobileOpen(false)}
            sx={{
              borderRadius: 2,
              mb: 0.5,
              '&.active': {
                bgcolor: 'action.selected',
                color: 'primary.main',
                '& .MuiListItemIcon-root': { color: 'primary.main' },
              },
            }}
          >
            <ListItemIcon sx={{ minWidth: 40 }}>
              <item.icon />
            </ListItemIcon>
            <ListItemText primary={item.label} />
          </ListItemButton>
        ))}
      </List>
    </Box>
  )

  return (
    <Box sx={{ display: 'flex', minHeight: '100vh' }}>
      <AppBar
        position="fixed"
        color="inherit"
        elevation={0}
        sx={{
          width: { md: `calc(100% - ${DRAWER_WIDTH}px)` },
          ml: { md: `${DRAWER_WIDTH}px` },
          bgcolor: 'background.paper',
          borderBottom: 1,
          borderColor: 'divider',
        }}
      >
        <Toolbar>
          <IconButton
            aria-label="Open navigation menu"
            edge="start"
            onClick={() => setMobileOpen(true)}
            sx={{ mr: 1, display: { md: 'none' } }}
          >
            <MenuIcon />
          </IconButton>

          <Typography variant="h6" component="h1" sx={{ flexGrow: 1, fontWeight: 600 }}>
            {title}
          </Typography>

          <Button
            color="inherit"
            onClick={(event) => setMenuAnchor(event.currentTarget)}
            aria-label="Open user menu"
            sx={{ textTransform: 'none' }}
            startIcon={
              <Avatar sx={{ width: 32, height: 32, bgcolor: 'primary.main', fontSize: 14 }}>
                {user.name.charAt(0).toUpperCase()}
              </Avatar>
            }
          >
            {user.name}
          </Button>
          <Menu anchorEl={menuAnchor} open={Boolean(menuAnchor)} onClose={() => setMenuAnchor(null)}>
            <MenuItem disabled>
              {user.name} ({user.role.toLowerCase()})
            </MenuItem>
            <Divider />
            <MenuItem
              onClick={() => {
                setMenuAnchor(null)
                logout()
              }}
            >
              <ListItemIcon>
                <LogoutIcon fontSize="small" />
              </ListItemIcon>
              Logout
            </MenuItem>
          </Menu>
        </Toolbar>
      </AppBar>

      {/* Drawer on narrow screens */}
      <Drawer
        variant="temporary"
        open={mobileOpen}
        onClose={() => setMobileOpen(false)}
        ModalProps={{ keepMounted: true }}
        sx={{
          display: { xs: 'block', md: 'none' },
          '& .MuiDrawer-paper': { width: DRAWER_WIDTH, boxSizing: 'border-box' },
        }}
      >
        {sidebar}
      </Drawer>

      {/* Permanent sidebar on desktop */}
      <Drawer
        variant="permanent"
        open
        sx={{
          display: { xs: 'none', md: 'block' },
          width: DRAWER_WIDTH,
          flexShrink: 0,
          '& .MuiDrawer-paper': { width: DRAWER_WIDTH, boxSizing: 'border-box' },
        }}
      >
        {sidebar}
      </Drawer>

      <Box component="main" sx={{ flexGrow: 1, minWidth: 0 }}>
        <Toolbar />
        <Box sx={{ p: 3, maxWidth: 1280, mx: 'auto' }}>
          {crumbs.length > 1 && (
            <Breadcrumbs aria-label="Breadcrumb" sx={{ mb: 2 }}>
              {crumbs.map((crumb, index) =>
                index < crumbs.length - 1 ? (
                  <Link key={crumb.to} component={RouterLink} to={crumb.to} underline="hover" color="inherit">
                    {crumb.label}
                  </Link>
                ) : (
                  <Typography key={crumb.to} color="text.primary">
                    {crumb.label}
                  </Typography>
                ),
              )}
            </Breadcrumbs>
          )}
          <Outlet />
        </Box>
      </Box>
    </Box>
  )
}
    