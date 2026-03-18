# Git Branching Guide

## Structure

main <- dev <- feature/database (Landon)
               feature/create-edit (Emad)
               feature/event-list (Omar Waled)
               feature/notifications (Omar Elkott)

main is for final, working code only.
dev is where everyone merges their work.
feature branches are where you do your actual work.

---

## First Time Setup

Clone the repo and create your feature branch off dev.

    git clone <repo-url>
    cd <repo>
    git checkout dev
    git checkout -b feature/your-branch
    git push origin feature/your-branch

---

## Daily Workflow

Before you start coding, sync with dev.

    git checkout dev
    git pull origin dev
    git checkout feature/your-branch
    git merge dev

Do your work, then commit and push.

    git add .
    git commit -m "short description of what you did"
    git push origin feature/your-branch

---

## Merging Into Dev

When your feature is ready, open a pull request on GitHub from your feature branch into dev.
Landon reviews and merges it.

To merge manually:

    git checkout dev
    git pull origin dev
    git merge feature/their-branch
    git push origin dev

---

## Merging Dev Into Main

Only when the app is fully tested and ready to submit.

    git checkout main
    git pull origin main
    git merge dev
    git push origin main

---

## Conflict Resolution

If there is a conflict, open the file and look for the conflict markers.

    <<<<<<< HEAD
    your version
    =======
    their version
    >>>>>>> feature/their-branch

Pick the right version, delete the markers, save the file, then:

    git add .
    git commit -m "resolve merge conflict in FileName.kt"

If the conflict is in anything database or model related, check with Landon before resolving.

---

## Notes

- Landon merges feature/database into dev first, before anyone else merges.
- Everyone pulls dev after that so they have the Event model and Repository available.
- Do not push directly to main or dev. Always go through a pull request.